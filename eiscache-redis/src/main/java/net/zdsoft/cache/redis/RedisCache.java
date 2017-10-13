package net.zdsoft.cache.redis;

import net.zdsoft.cache.configuration.Configuration;
import net.zdsoft.cache.core.Cache;
import net.zdsoft.cache.expiry.Duration;
import net.zdsoft.cache.transfer.ByteTransfer;
import net.zdsoft.cache.transfer.ValueTransfer;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.ReturnType;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 *    redis缓存实现，以key-value的形式缓存数据<br>
 *    通过KEY_SET_NAME---key缓存所有的key值  id-key -- set<key> 的形式将entityId和key对应<br>
 *    更新缓存时利用entityId更新相关的所有缓存
 * </p>
 * @author shenke
 * @since 2017.09.04
 */
public class RedisCache implements Cache {

    private Logger logger = Logger.getLogger(RedisCache.class);

    /**详细说明参见addKeyByEntityId.lua*/
    private static final String ADD_ID_KEY_SCRIPT = "local entityIdArray=ARGV[1]; local entityIds = loadstring(\"return \"..entityIdArray)(); local count = #entityIds; if ( count > 0 ) then for index, entityId in ipairs(entityIds) do redis.call('SADD', ARGV[2]..entityId, KEYS[1]); end; end;return count;";
    /**详细说明参见net/zdsoft/cache/lua/delKeyByEntityId.lua*/
    private static final String DEL_KEY_BY_IDS = "local entity_id_table = ARGV[1]; local entity_ids = loadstring(\"return \"..entity_id_table)(); local id_key_prefix = ARGV[2]; local key_set_name = ARGV[3]; local desc_key_table = ARGV[3]; local desc_keys = loadstring(\"return \"..entity_id_table)(); local count = #entity_ids;\n" +
            "if ( count > 0 ) then for index, entityId in ipairs(entity_ids) do local allKey = redis.call('SMEMBERS', id_key_prefix..entityId); if ( #allKey > 0 ) then for _, val in ipairs(allKey) do redis.call('DEL', val); redis.call('ZREM', key_set_name, val); end; end; redis.call('DEL', id_key_prefix..entityId); end; end;\n" +
            "if ( #desc_keys > 0 ) then for _, key in ipairs(desc_keys) do redis.call('DEL', key); end; end; return count + #desc_keys;";


    /**自增操作，需给定指定步长，若key不存在返回-1*/
    private static final String INCR_BY_NOT_KEY_ERROR = "local existsKey = redis.call('EXISTS', KEYS[1]);if ( existsKey > 0 ) then return redis.call('INCRBY', KEYS[1], ARGV[1]); else return -1;end;";
    private static final String PUT_IF_ABSENT = "local existsKey = redis.call('EXISTS', KEYS[1]); if ( existsKey == 0 ) then return redis.call('SET', KEYS[1], ARGV[1]); else return redis.call('GET', KEYS[1]); end;";

    private Configuration cacheConfiguration;
    private String name;
    private String cacheGlobalPrefix;
    private RedisTemplate redisTemplate;
    private String clearScript;

    private ByteTransfer byteTransfer;
    private ValueTransfer valueTransfer;

    private byte[] KEY_SET_NAME;
    private byte[] ID_KEY_PREFIX;

    protected RedisCache(RedisTemplate redisTemplate, String name, String cacheGlobalPrefix, Configuration configuration) {
        this.cacheConfiguration = configuration;
        this.byteTransfer = configuration.getByteTransfer();
        this.valueTransfer = configuration.getValueTransfer();
        this.redisTemplate = redisTemplate;
        this.name = name;
        this.cacheGlobalPrefix = cacheGlobalPrefix;
        this.KEY_SET_NAME = byteTransfer.transfer(cacheGlobalPrefix+ "." + name + "<~>keys");
        this.clearScript = "redis.call('del', unpack(redis.call('keys','"+ this.cacheGlobalPrefix + "." + name +".*')))";
        ID_KEY_PREFIX = byteTransfer.transfer(cacheGlobalPrefix + "." + name + ".id-key.");
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Object getNativeCache() {
        return redisTemplate;
    }

    @Override
    public Cache.CacheWrapper get(final Object key) {
        Object object = redisTemplate.<Cache.CacheWrapper>execute(new RedisCallback<Cache.CacheWrapper>() {
            @Override
            public Cache.CacheWrapper doInRedis(RedisConnection connection) throws DataAccessException {
                byte[] keyBytes = getKey(key);
                byte[] value = connection.get(keyBytes);
                return buildWrapper(byteTransfer.transfer(value));
            }
        }, true);
        return (Cache.CacheWrapper) object;
    }

    @Override
    public void remove(final Set<String> entityId, final Object key) {
        redisTemplate.execute(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                if ( entityId == null || entityId.isEmpty() ) {
                    connection.del(getKey(key));
                    return null;
                }
                connection.del(getKey(key));
                connection.eval(byteTransfer.transfer(DEL_KEY_BY_IDS), ReturnType.INTEGER, 0,
                        buildEntityIdsArgv(entityId), ID_KEY_PREFIX, KEY_SET_NAME, buildKeyArv(key));
                return null;
            }
        },true);
    }

    @Override
    public void remove(final Set<String> entityId, final Object... keys) {
        redisTemplate.execute(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                if ( entityId == null || entityId.isEmpty() ) {
                    connection.del(getKey(keys).toArray(new byte[keys.length][]));
                    return null;
                }
                connection.eval(byteTransfer.transfer(DEL_KEY_BY_IDS), ReturnType.INTEGER, 0,
                        buildEntityIdsArgv(entityId), ID_KEY_PREFIX, KEY_SET_NAME, buildKeyArv(keys));
                return null;
            }
        });
    }

    @Override
    public void removeAll() {
        redisTemplate.execute(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                try {
                    connection.eval(byteTransfer.transfer(clearScript), ReturnType.INTEGER, 0, getKey(""));
                } catch (Exception e) {
                    logger.error("clear cache " + name + " use lua script error, try again(use del foreach)", e);
                    Set<byte[]> keySets = connection.zRange(KEY_SET_NAME, 0, -1);
                    keySets.add(KEY_SET_NAME);
                    connection.del(keySets.toArray(new byte[keySets.size()][]));
                }
                return null;
            }
        });
    }

    @Override
    public <T> T get(Object key, Callable<T> valueLoader) {
        return null;
    }

    @Override
    public void put(final Set<String> entityId, final Object key, final Object value) {
        redisTemplate.execute(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                byte[] keyBytes = getKey(key);
                connection.set(keyBytes, byteTransfer.transfer(valueTransfer.transfer(value)));
                connection.eval(byteTransfer.transfer(ADD_ID_KEY_SCRIPT), ReturnType.INTEGER, 1,
                        getKey(key), buildEntityIdsArgv(entityId), ID_KEY_PREFIX);
                connection.zAdd(KEY_SET_NAME, 0 , keyBytes);
                return null;
            }
        });
    }

    private byte[] buildEntityIdsArgv(Set<String> ids) {
        if ( ids == null || ids.isEmpty() ) {
            return byteTransfer.transfer("{}");
        }
        StringBuilder argvBuilder = new StringBuilder();
        argvBuilder.append("{");
        for (String id : ids) {
            argvBuilder.append("\"").append(id).append("\",");
        }
        argvBuilder.append("}");
        return byteTransfer.transfer(argvBuilder.toString());
    }

    private byte[] buildKeyArv(Object ... keys) {
        StringBuilder keyArgv = new StringBuilder();
        keyArgv.append("{");
        if ( keys != null ) {
            for (Object key : keys) {
                keyArgv.append("\"").append(valueTransfer.transfer(key)).append("\",");
            }
        }
        keyArgv.append("}");
        return byteTransfer.transfer(keyArgv.toString());
    }

    @Override
    public void put(final Set<String> entityId, final Object key, final Object value, final long seconds) {
        redisTemplate.execute(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                byte[] keyBytes = getKey(key);
                connection.setEx(keyBytes, seconds, byteTransfer.transfer(valueTransfer.transfer(value)));
                connection.eval(byteTransfer.transfer(ADD_ID_KEY_SCRIPT), ReturnType.INTEGER,
                        1, getKey(key), buildEntityIdsArgv(entityId), ID_KEY_PREFIX);
                connection.zAdd(KEY_SET_NAME, 0 , keyBytes);
                return null;
            }
        });
    }

    @Override
    public void put(final Set<String> entityId, final Object key, final Object value, final int account, final TimeUnit timeUnit) {
        if ( account == 0 ) {
            if ( Duration.NEVER.equals(getConfiguration().getExpiry().getCreateExpire()) ) {
                put(entityId, key, value);
            }
            else {
                put(entityId, key, value, getConfiguration().getExpiry().getCreateExpire().toSeconds());
            }
            return ;
        }
        redisTemplate.execute(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                byte[] keyBytes = getKey(key);
                if ( TimeUnit.MICROSECONDS.equals(timeUnit) ) {
                    connection.pSetEx(keyBytes, timeUnit.toMillis(account), byteTransfer.transfer(valueTransfer.transfer(value)));
                } else {
                    connection.setEx(keyBytes, timeUnit.toSeconds(account), byteTransfer.transfer(valueTransfer.transfer(value)));
                }
                connection.eval(byteTransfer.transfer(ADD_ID_KEY_SCRIPT), ReturnType.INTEGER,
                        1, getKey(key), buildEntityIdsArgv(entityId), ID_KEY_PREFIX);
                connection.zAdd(KEY_SET_NAME, 0 , keyBytes);
                return null;
            }
        });
    }

    @Override
    public Object getNative(final Object key) {
        return redisTemplate.execute(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                byte[] bytes = connection.get(getKey(key));
                return byteTransfer.transfer(bytes);
            }
        });
    }

    @Override
    public Cache.CacheWrapper putIfAbsent(final Object key, final Object value) {
        Object result = redisTemplate.execute(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                return connection.eval(byteTransfer.transfer(PUT_IF_ABSENT), ReturnType.STATUS, 1,
                        getKey(key), byteTransfer.transfer(valueTransfer.transfer(value)));
            }
        });
        //FIXME 根据connection.eval() 代码看 result应该是byte[] 但是实际得到的结果并不是 （api需要细看）
        Object returnString = result instanceof byte[] ? redisTemplate.getKeySerializer().deserialize((byte[]) result) : result;
        if ( "OK".equals(returnString) ) {
            return new CacheWrapper(valueTransfer.transfer(value));
        } else {
            return new CacheWrapper(returnString.toString());
        }
    }

    @Override
    public Configuration getConfiguration() {
        return this.cacheConfiguration;
    }

    @Override
    public void destroy() {
        removeAll();
    }

    @Override
    public long incrBy(final Object key, final long value) {
        Object val = redisTemplate.<Object>execute(new RedisCallback<Long>() {
            @Override
            public Long doInRedis(RedisConnection connection) throws DataAccessException {
                return connection.eval(byteTransfer.transfer(INCR_BY_NOT_KEY_ERROR),ReturnType.INTEGER,
                        1, getKey(key), String.valueOf(value).getBytes());
            }
        });
        return (Long)val;
    }

    private List<byte[]> getKey(Object ... keys) {
        List<byte[]> keyBytes = new ArrayList<byte[]>(keys.length);
        for (Object key : keys) {
            keyBytes.add(getKey(key));
        }
        return keyBytes;
    }

    private byte[] getKey(Object key) {
        byte[] keyBytes = null;
        if ( getConfiguration().getKeyType().equals(String.class) ) {
            keyBytes = convertToByteIfNecessary(cacheGlobalPrefix + "." + name + "." + key.toString());
        }
        if ( keyBytes == null ) {
            keyBytes = convertToByteIfNecessary(key);
        }
        return keyBytes;
    }

    private byte[] convertToByteIfNecessary (Object key) {
        if ( key != null && key instanceof byte[]) {
            return (byte[]) key;
        }
        if ( key != null && String.class.equals(key.getClass()) ) {
            return byteTransfer.transfer(key.toString());
        }
        return byteTransfer.transfer(valueTransfer.transfer(key));
    }

    class CacheWrapper implements Cache.CacheWrapper{
        private String value;

        private CacheWrapper(String value) {
            this.value = value;
        }

        public <T> T getEntity(Class<T> tClass) {
            return getConfiguration().getValueTransfer().parseForNative(value, tClass);
        }

        public <K,V> Map<K,V> getMap(Type kClass, Type vClass) {
            return getConfiguration().getValueTransfer().parseFor(value, kClass, vClass);
        }

        public <T> List<T> getList(Type tClass) {
            return getConfiguration().getValueTransfer().parseForList(value, tClass);
        }

        public <T> Set<T> getSet(Type tClass) {
            return getConfiguration().getValueTransfer().parseForSet(value, tClass);
        }

        @Override
        public <K, V> Map<K, V> getMap(Type genericType) {
            return getConfiguration().getValueTransfer().parseForNative(value, genericType);
        }

        @Override
        public <T> T get(Type genericType) {
            return getConfiguration().getValueTransfer().parseForNative(value, genericType);
        }
    }

    private Cache.CacheWrapper buildWrapper(String value) {
        return new CacheWrapper(value);
    }
}