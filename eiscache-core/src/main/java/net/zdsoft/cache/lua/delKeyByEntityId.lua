--
-- Created by IntelliJ IDEA.
-- User: shenke
-- Date: 2017-9-18
-- Time: 15:18
-- To change this template use File | Settings | File Templates.
-- 根据entityId 删除 key

local entityIds = {#ENTITY_IDS};  --运行时替换#ENTITY_IDS
local count = #entityIds;
if ( count > 0 ) then
    for index, entityId in ipairs(entityIds) --遍历entityId，获取每个entityId缓存的key集合
        do
        local allKey = redis.call('SMEMBERS', '#ID_KEY_PREFIX'..entityId); -- 运行时替换#ID_KEY_PREFIX
        if ( #allKey > 0 ) then
            for _, val in ipairs(allKey)                  -- 遍历key，删除指定key的缓存，删除keySetName中的缓存
                do
                redis.call('DEL', val);                   -- 根据key删除
                redis.call('ZREM', '#KEY_SET_NAME', val); --运行时替换，删除keySetName中的数据
            end;
        end;
        redis.call('DEL', '#ID_KEY_PREFIX'..entityId); -- 删除存放key的缓存
    end;
end;
-- return count;