--
-- Created by IntelliJ IDEA.
-- User: shenke
-- Date: 2017-9-18
-- Time: 15:26
-- To change this template use File | Settings | File Templates.
-- 根据entityId 和 key更新存放key的缓存

local entityIds = ARGV[1];  --运行时替换#ENTITY_IDS
local id_key_prefix = ARGV[2];
local count = #entityIds;
if ( count > 0 ) then
    for index, entityId in ipairs(entityIds)  --遍历entityId， 更新key
        do
        redis.call('SADD', id_key_prefix..entityId, KEYS[1]);
    end;
end;
return count;
