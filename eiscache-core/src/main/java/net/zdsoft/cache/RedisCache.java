package net.zdsoft.cache;

import com.alibaba.fastjson.JSON;
import net.zdsoft.cache.configuration.CacheConfiguration;
import net.zdsoft.cache.utils.ReturnTypeContext;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.ReturnType;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
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

    /**
     * 详细说明参见addKeyByEntityId.lua
     */
    private String ADD_ID_KEY = "local entityIds = {#ENTITY_IDS}; local count = #entityIds; if ( count > 0 ) then for index, entityId in ipairs(entityIds) do redis.call('SADD', '#ID_KEY_PREFIX'..entityId, KEYS[1]); end; end;return count;";
    public static final String R_ENTITY_IDS = "#ENTITY_IDS";
    public static final String R_KEY = "#KEY";
    public static final String R_ID_KEY_PREFIX = "#ID_KEY_PREFIX";

    /**
     * 详细说明参见
     * net/zdsoft/cache/lua/delKeyByEntityId.lua
     */
    private String DEL_BY_ENTITYIDS = "local entityIds = {#ENTITY_IDS}; local count = #entityIds; if ( count > 0 ) then for index, entityId in ipairs(entityIds) do local allKey = redis.call('SMEMBERS', '#ID_KEY_PREFIX'..entityId); if ( #allKey > 0 ) then for k,val in ipairs(allKey) do redis.call('DEL', val); redis.call('ZREM','#KEY_SET_NAME', val); end; end; redis.call('DEL', '#ID_KEY_PREFIX'..entityId); end; end; ";

    private CacheConfiguration cacheConfiguration;
    private String name;
    private String cacheGlobalPrefix;
    private RedisTemplate redisTemplate;
    public String clearScript;

    private byte[] KEY_SET_NAME;
    private String ID_KEY_PREFIX;

    public RedisCache(RedisTemplate redisTemplate, String name, String cacheGlobalPrefix) {
        this.redisTemplate = redisTemplate;
        this.name = name;
        this.cacheGlobalPrefix = cacheGlobalPrefix;
        this.KEY_SET_NAME = redisTemplate.getKeySerializer().serialize(cacheGlobalPrefix+ "." + name + "<~>keys");
        this.clearScript = "redis.call('del', unpack(redis.call('keys','"+ this.cacheGlobalPrefix + "." + name +".*')))";;
        //ID_KEY_MAP_NAME = redisTemplate.getKeySerializer().serialize(name + "." + "id-key-map-name");
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
    public <T> T get(Object key, Class<T> type) {
        Object value = redisTemplate.execute(new RedisCallback<T>() {
            @Override
            public T doInRedis(RedisConnection connection) throws DataAccessException {
                byte[] keyBytes = getKey(key);
                byte[] value = connection.get(keyBytes);
                if ( value == null ) {
                    return null;
                }
                if (native_type.contains(type)) {
                    return (T) redisTemplate.getValueSerializer().deserialize(value);
                }
                if ( !NATIVE_COLLECTION_TYPE.contains(type) && !ReturnTypeContext.getEntityType().equals(ReturnTypeContext.getReturnType()) ) {
                    return JSON.parseObject(new String(value), (Class<T>) ReturnTypeContext.getEntityType());
                }
                return JSON.parseObject(new String(value), type);
            }
        }, true);
        return (T) value;
    }

    @Override
    public void remove(Set<String> entityId, Object key) {
        redisTemplate.execute(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                connection.del(getKey(key));
                String delScript = DEL_BY_ENTITYIDS.replace(R_ENTITY_IDS, buildEntityIds2LuaArray(entityId))
                        .replace(R_ID_KEY_PREFIX, ID_KEY_PREFIX)
                        .replace("#KEY_SET_NAME", cacheGlobalPrefix + "." + name + "<~>keys");
                delScript += "redis.call('DEL',KEYS[1]); return count;";
                connection.eval(redisTemplate.getKeySerializer().serialize(delScript), ReturnType.INTEGER, 1, getKey(key == null ? "null" : key));
                return null;
            }
        },true);
    }

    private String buildEntityIds2LuaArray(Set<String> entityIds) {
        StringBuilder luaArray = new StringBuilder("");
        for (String id : entityIds) {
            luaArray.append("\"").append(id).append("\",");
        }
        luaArray.replace(luaArray.length()-1, luaArray.length(), "");
        return luaArray.toString();
    }

    @Override
    public void remove(Set<String> entityId, Object... keys) {
        redisTemplate.execute(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {

                connection.del(getKey(keys).toArray(new byte[0][]));
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
                connection.eval(redisTemplate.getKeySerializer().serialize(scriptBuffer.toString()), ReturnType.INTEGER, index, getKey(keys).toArray(new byte[index][]));
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
                    connection.eval(redisTemplate.getKeySerializer().serialize(clearScript), ReturnType.INTEGER, 0, getKey(""));
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

    private Map<byte[], byte[]> buildIDKeyMap(Set<String> entityIds, byte[] keyBytes) {
        Map<byte[], byte[]> IDKeyMap = new HashMap<>();
        for (String id : entityIds) {
            IDKeyMap.put(redisTemplate.getHashKeySerializer().serialize(id), keyBytes);
        }
        return IDKeyMap;
    }

    @Override
    public void put(Set<String> entityId, Object key, Object value) {
        redisTemplate.execute(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                byte[] keyBytes = getKey(key);
                connection.set(keyBytes, redisTemplate.getValueSerializer().serialize(value));
                String addIDKeyScript = ADD_ID_KEY.replace(R_ENTITY_IDS, buildEntityIds2LuaArray(entityId))
                        .replace(R_ID_KEY_PREFIX, ID_KEY_PREFIX);
                connection.eval(redisTemplate.getKeySerializer().serialize(addIDKeyScript), ReturnType.INTEGER, 1, getKey(key));
                //connection.hMSet(ID_KEY_MAP_NAME, buildIDKeyMap(entityId, keyBytes));
                connection.zAdd(KEY_SET_NAME, 0 , keyBytes);
                return null;
            }
        });
    }

    @Override
    public void put(Set<String> entityId, Object key, Object value, long seconds) {
        redisTemplate.execute(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                byte[] keyBytes = getKey(key);
                connection.setEx(keyBytes, seconds, redisTemplate.getValueSerializer().serialize(value));
                String addIDKeyScript = ADD_ID_KEY.replace(R_ENTITY_IDS, buildEntityIds2LuaArray(entityId))
                        .replace(R_ID_KEY_PREFIX, ID_KEY_PREFIX);
                connection.eval(redisTemplate.getKeySerializer().serialize(addIDKeyScript), ReturnType.INTEGER, 1, getKey(key));
                //connection.hMSet(ID_KEY_MAP_NAME, buildIDKeyMap(entityId, keyBytes));
                connection.zAdd(KEY_SET_NAME, 0 , keyBytes);
                return null;
            }
        });
    }

    @Override
    public void put(Set<String> entityId, Object key, Object value, int account, TimeUnit timeUnit) {
        if ( account == 0 ) {
            put(entityId, key, value);
            return ;
        }
        redisTemplate.execute(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                byte[] keyBytes = getKey(key);
                if ( TimeUnit.MICROSECONDS.equals(timeUnit) ) {
                    connection.pSetEx(keyBytes, timeUnit.toMillis(account), redisTemplate.getValueSerializer().serialize(value));
                } else {
                    connection.setEx(keyBytes, timeUnit.toSeconds(account), redisTemplate.getValueSerializer().serialize(value));
                }
                String addIDKeyScript = ADD_ID_KEY.replace(R_ENTITY_IDS, buildEntityIds2LuaArray(entityId))
                        .replace(R_ID_KEY_PREFIX, ID_KEY_PREFIX);
                connection.eval(redisTemplate.getKeySerializer().serialize(addIDKeyScript), ReturnType.INTEGER, 1, getKey(key));
                //connection.hMSet(ID_KEY_MAP_NAME, buildIDKeyMap(entityId, keyBytes));
                connection.zAdd(KEY_SET_NAME, 0 , keyBytes);
                return null;
            }
        });
    }

    @Override
    public Object get(Object key) {

        return null;
    }

    @Override
    public Object putIfAbsent(Object key, Object value, Class<?> type) {
        return null;
    }

    @Override
    public <C extends CacheConfiguration> C getConfiguration() {
        return (C) this.cacheConfiguration;
    }

    @Override
    public void destroy() {

    }

    private List<byte[]> getKey(Object ... keys) {
        List<byte[]> keyBytes = new ArrayList<>(keys.length);
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
        return serializer.serialize(key);
    }

    private static final Set<Class<?>> native_type = new HashSet<Class<?>>(16){{
        add(int.class);
        add(Integer.class);
        add(float.class);
        add(Float.class);
        add(char.class);
        add(Character.class);
        add(double.class);
        add(Double.class);
        add(String.class);
        add(boolean.class);
        add(Boolean.class);
    }};

    private static final Set<Class<?>> NATIVE_COLLECTION_TYPE = new HashSet<Class<?>>(){{
       add(Set.class);
       add(List.class);
       add(ArrayList.class);
       add(HashSet.class);
       add(Map.class);
    }};

    public void setCacheConfiguration(CacheConfiguration cacheConfiguration) {
        this.cacheConfiguration = cacheConfiguration;
    }
}
