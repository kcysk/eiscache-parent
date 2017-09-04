package net.zdsoft.cache.configuration;

import com.alibaba.fastjson.JSON;

/**
 * @author shenke
 * @since 2017.09.04
 */
public class JSONValueTransfer<T> implements ValueTransfer<String, T> {


    @Override
    public T parse(String object, Class<T> tClass) {
        return JSON.parseObject(object, tClass);
    }

    @Override
    public String transfer(Object object) {
        return JSON.toJSONString(object);
    }
}
