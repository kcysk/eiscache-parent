--
-- Created by IntelliJ IDEA.
-- User: shenke
-- Date: 2017-9-19
-- Time: 15:18
-- To change this template use File | Settings | File Templates.
-- 自增（兼容6.0 5.0原有memcache写法，当key不存在时返回 -1L）

local existsKey = redis.call('EXISTS', KEYS[1]);
if ( existsKey ) then
    return redis.call('INCRBY', KEYS[1], ARGV[1]);
else
    return -1;
end;