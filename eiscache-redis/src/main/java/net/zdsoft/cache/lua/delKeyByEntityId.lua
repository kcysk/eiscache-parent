--
-- Created by IntelliJ IDEA.
-- User: shenke
-- Date: 2017-9-18
-- Time: 15:18
-- To change this template use File | Settings | File Templates.
-- 根据entityId 删除 key

local entity_id_table = ARGV[1];
local entity_ids = loadstring("return "..entity_id_table)();  --运行时替换#ENTITY_IDS
local id_key_prefix = ARGV[2];
local key_set_name = ARGV[3];
local desc_key_table = ARGV[3];
local desc_keys = loadstring("return "..entity_id_table)();
local count = #entity_ids;
if ( count > 0 ) then
    for index, entityId in ipairs(entity_ids) --遍历entityId，获取每个entityId缓存的key集合
        do
        local allKey = redis.call('SMEMBERS', id_key_prefix..entityId); -- 运行时替换#ID_KEY_PREFIX
        if ( #allKey > 0 ) then
            for _, val in ipairs(allKey)                  -- 遍历key，删除指定key的缓存，删除keySetName中的缓存
                do
                redis.call('DEL', val);                   -- 根据key删除
                redis.call('ZREM', key_set_name, val); --运行时替换，删除keySetName中的数据
            end;
        end;
        redis.call('DEL', id_key_prefix..entityId); -- 删除存放key的缓存
    end;
end;
if ( #desc_keys > 0 ) then
    for _, key in ipairs(desc_keys)
        do
        redis.call('DEL', key);
    end;
end
return count + #desc_keys;