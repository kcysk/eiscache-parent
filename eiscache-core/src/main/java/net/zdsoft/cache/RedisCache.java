package net.zdsoft.cache;

import com.alibaba.fastjson.JSON;
import net.zdsoft.cache.configuration.ByteTransfer;
import net.zdsoft.cache.configuration.CacheConfiguration;
import net.zdsoft.cache.configuration.ValueTransfer;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.ReturnType;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;

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
public class RedisCache implements Cache{

    private Logger logger = Logger.getLogger(RedisCache.class);

    /**详细说明参见addKeyByEntityId.lua*/
    private String ADD_ID_KEY = "local entityIds = {#ENTITY_IDS}; local count = #entityIds; if ( count > 0 ) then for index, entityId in ipairs(entityIds) do redis.call('SADD', '#ID_KEY_PREFIX'..entityId, KEYS[1]); end; end;return count;";
    /**详细说明参见net/zdsoft/cache/lua/delKeyByEntityId.lua*/
    private String DEL_BY_ENTITYIDS = "local entityIds = {#ENTITY_IDS}; local count = #entityIds; if ( count > 0 ) then for index, entityId in ipairs(entityIds) do local allKey = redis.call('SMEMBERS', '#ID_KEY_PREFIX'..entityId); if ( #allKey > 0 ) then for k,val in ipairs(allKey) do redis.call('DEL', val); redis.call('ZREM','#KEY_SET_NAME', val); end; end; redis.call('DEL', '#ID_KEY_PREFIX'..entityId); end; end; ";
    /**自增操作，需给定指定步长，若key不存在返回-1*/
    private static final String INCR_BY_NOT_KEY_ERROR = "local existsKey = redis.call('EXISTS', KEYS[1]);if ( existsKey > 0 ) then return redis.call('INCRBY', KEYS[1], ARGV[1]); else return -1;end;";
    private static final String PUT_IF_ABSENT = "local existsKey = redis.call('EXISTS', KEYS[1]); if ( existsKey == 0 ) then return redis.call('SET', KEYS[1], ARGV[1]); else return redis.call('GET', KEYS[1]); end;";

    private static final String R_ENTITY_IDS = "#ENTITY_IDS";
    private static final String R_ID_KEY_PREFIX = "#ID_KEY_PREFIX";


    private CacheConfiguration cacheConfiguration;
    private String name;
    private String cacheGlobalPrefix;
    private RedisTemplate redisTemplate;
    private String clearScript;

    private ByteTransfer byteTransfer;
    private ValueTransfer valueTransfer;

    private byte[] KEY_SET_NAME;
    private String ID_KEY_PREFIX;

    public RedisCache(RedisTemplate redisTemplate, String name, String cacheGlobalPrefix, ByteTransfer byteTransfer, ValueTransfer valueTransfer) {
        this.byteTransfer = byteTransfer;
        this.valueTransfer = valueTransfer;
        this.redisTemplate = redisTemplate;
        this.name = name;
        this.cacheGlobalPrefix = cacheGlobalPrefix;
        this.KEY_SET_NAME = byteTransfer.transfer(cacheGlobalPrefix+ "." + name + "<~>keys");
        this.clearScript = "redis.call('del', unpack(redis.call('keys','"+ this.cacheGlobalPrefix + "." + name +".*')))";;
        ID_KEY_PREFIX = cacheGlobalPrefix + "." + name + ".id-key.";
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
                String delScript = DEL_BY_ENTITYIDS.replace(R_ENTITY_IDS, buildEntityIds2LuaArray(entityId))
                        .replace(R_ID_KEY_PREFIX, ID_KEY_PREFIX)
                        .replace("#KEY_SET_NAME", cacheGlobalPrefix + "." + name + "<~>keys");
                delScript += "redis.call('DEL',KEYS[1]); return count;";
                connection.eval(byteTransfer.transfer(delScript), ReturnType.INTEGER, 1, getKey(key == null ? "null" : key));
                return null;
            }
        },true);
    }

    private String buildEntityIds2LuaArray(Set<String> entityIds) {
        if ( entityIds == null || entityIds.isEmpty() ) {
            return "";
        }
        StringBuilder luaArray = new StringBuilder("");
        for (String id : entityIds) {
            luaArray.append("\"").append(id).append("\",");
        }
        luaArray.replace(luaArray.length()-1, luaArray.length(), "");
        return luaArray.toString();
    }

    @Override
    public void remove(final Set<String> entityId, final Object... keys) {
        redisTemplate.execute(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                //connection.del(getKey(keys).toArray(new byte[0][]));
                if ( entityId == null || entityId.isEmpty() ) {
                    connection.del(getKey(keys).toArray(new byte[keys.length][]));
                    return null;
                }
                String delScript = DEL_BY_ENTITYIDS.replace(R_ENTITY_IDS, buildEntityIds2LuaArray(entityId))
                        .replace(R_ID_KEY_PREFIX, ID_KEY_PREFIX)
                        .replace("#KEY_SET_NAME", cacheGlobalPrefix + "." + name + "<~>keys");
                StringBuilder scriptBuffer = new StringBuilder(delScript);
                scriptBuffer.append("\"redis.call('DEL',");
                int index = 1;
                for (Object key : keys) {
                    scriptBuffer.append("ARGV[1]");
                    if ( index != keys.length ) {
                        scriptBuffer.append(",");
                        index ++;
                    } else {
                        scriptBuffer.append(");return count;");
                    }
                }
                connection.eval(byteTransfer.transfer(scriptBuffer.toString()), ReturnType.INTEGER, index, getKey(keys).toArray(new byte[index][]));
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
                    logger.error("clear cache " + name + " use lua script error, try again(use jedis client)", e);
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
                String addIDKeyScript = ADD_ID_KEY.replace(R_ENTITY_IDS, buildEntityIds2LuaArray(entityId))
                        .replace(R_ID_KEY_PREFIX, ID_KEY_PREFIX);
                connection.eval(byteTransfer.transfer(addIDKeyScript), ReturnType.INTEGER, 1, getKey(key));
                connection.zAdd(KEY_SET_NAME, 0 , keyBytes);
                return null;
            }
        });
    }

    @Override
    public void put(final Set<String> entityId, final Object key, final Object value, final long seconds) {
        redisTemplate.execute(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                byte[] keyBytes = getKey(key);
                connection.setEx(keyBytes, seconds, byteTransfer.transfer(valueTransfer.transfer(value)));
                String addIDKeyScript = ADD_ID_KEY.replace(R_ENTITY_IDS, buildEntityIds2LuaArray(entityId))
                        .replace(R_ID_KEY_PREFIX, ID_KEY_PREFIX);
                connection.eval(byteTransfer.transfer(addIDKeyScript), ReturnType.INTEGER, 1, getKey(key));
                //connection.hMSet(ID_KEY_MAP_NAME, buildIDKeyMap(entityId, keyBytes));
                connection.zAdd(KEY_SET_NAME, 0 , keyBytes);
                return null;
            }
        });
    }

    @Override
    public void put(final Set<String> entityId, final Object key, final Object value, final int account, final TimeUnit timeUnit) {
        if ( account == 0 ) {
            put(entityId, key, value);
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
                String addIDKeyScript = ADD_ID_KEY.replace(R_ENTITY_IDS, buildEntityIds2LuaArray(entityId))
                        .replace(R_ID_KEY_PREFIX, ID_KEY_PREFIX);
                connection.eval(byteTransfer.transfer(addIDKeyScript), ReturnType.INTEGER, 1, getKey(key));
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
                return connection.eval(byteTransfer.transfer(PUT_IF_ABSENT), ReturnType.STATUS, 1, getKey(key), byteTransfer.transfer(valueTransfer.transfer(value)));
            }
        });
        if ( result instanceof byte[] ) {
            Object returnString = redisTemplate.getKeySerializer().deserialize((byte[]) result);
            if ( "OK".equals(returnString) ) {
                return new CacheWrapper(JSON.toJSONString(value));
            } else {
                return new CacheWrapper(returnString.toString());
            }
        }
        return new CacheWrapper(JSON.toJSONString(value));
    }

    @Override
    public <C extends CacheConfiguration> C getConfiguration() {
        return (C) this.cacheConfiguration;
    }

    @Override
    public void destroy() {
        removeAll();
    }

    @Override
    public long incrBy(final Object key, final int value) {
        Object val = redisTemplate.<Object>execute(new RedisCallback<Long>() {
            @Override
            public Long doInRedis(RedisConnection connection) throws DataAccessException {
                return connection.eval(byteTransfer.transfer(INCR_BY_NOT_KEY_ERROR),ReturnType.INTEGER, 1, getKey(key), String.valueOf(value).getBytes());
            }
        });
        return (Long)val;
    }

    @Override
    public ValueTransfer getTransfer() {
        return valueTransfer;
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
            keyBytes = convertToByteIfNecessary(cacheGlobalPrefix + "." + name + "." + key.toString() , redisTemplate.getKeySerializer());
        }
        if ( keyBytes == null ) {
            keyBytes = convertToByteIfNecessary(key, redisTemplate.getKeySerializer());
        }
        return keyBytes;
    }

    private byte[] convertToByteIfNecessary (Object key, RedisSerializer serializer) {
        if ( key != null && key instanceof byte[]) {
            return (byte[]) key;
        }
        if ( String.class.equals(key.getClass()) ) {
            return byteTransfer.transfer(key.toString());
        }
        return byteTransfer.transfer(valueTransfer.transfer(key));
    }

    public void setCacheConfiguration(CacheConfiguration cacheConfiguration) {
        this.cacheConfiguration = cacheConfiguration;
    }

    class CacheWrapper implements Cache.CacheWrapper{
        private String value;

        public CacheWrapper(String value) {
            this.value = value;
        }

        public <T> T getEntity(Class<T> tClass) {
            return RedisCache.this.getTransfer().parseForNative(value, tClass);
        }

        public <K,V> Map<K,V> getMap(Type kClass, Type vClass) {
            return RedisCache.this.getTransfer().parseFor(value, kClass, vClass);
        }

        public <T> List<T> getList(Type tClass) {
            return RedisCache.this.getTransfer().parseForList(value, tClass);
        }

        public <T> Set<T> getSet(Type tClass) {
            return RedisCache.this.getTransfer().parseForSet(value, tClass);
        }

        @Override
        public <K, V> Map<K, V> getMap(Type genericType) {
            return RedisCache.this.getTransfer().parseForNative(value, genericType);
        }

        @Override
        public <T> T get(Type genericType) {
            return RedisCache.this.getTransfer().parseForNative(value, genericType);
        }
    }

    public Cache.CacheWrapper buildWrapper(String value) {
        return new CacheWrapper(value);
    }
}
