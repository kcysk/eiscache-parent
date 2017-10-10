package net.zdsoft.cache;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import net.zdsoft.cache.entity.User;
import net.zdsoft.cache.service.BaseService;
import net.zdsoft.cache.utils.BeanUtils;
import org.junit.Test;

/**
 * @author shenke
 * @since 2017.09.27
 */
public class BeanUtilsTest {

    //@Test
    public void testGetGenericType() {
        String s = "oa7.szxy.edu88.com";
        System.out.println(JSON.toJSONString(s));
    }

    //@Test
    public void testTypeDerivation() {
        testType(new GenericInterface<User>() {
        });
    }

    public <T> void testType(GenericInterface<T> tGenericInterface) {
        //testType(new GenericInterface<T>() {});
    }

    interface GenericInterface<T> {

    }
}
