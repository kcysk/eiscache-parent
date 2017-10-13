package net.zdsoft.cache;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import net.zdsoft.cache.entity.User;
import net.zdsoft.cache.service.BaseService;
import net.zdsoft.cache.utils.BeanUtils;
import org.junit.Test;

import java.util.List;

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
    public static void main(String[] args) {
        GenericClass<List<User>> genericClass = new GenericClass<List<User>>() {};
        System.out.println(BeanUtils.getFirstGenericType(genericClass.getClass()));
    }

    public <T> void testType(GenericInterface tGenericInterface) {
        //testType(new GenericInterface<T>() {});
    }


}
abstract class GenericInterface<T> {

}

abstract class GenericClass<T> implements TypeInterface<T>, TypeInterfaceTwo {

}

interface TypeInterface<T> {

}

interface TypeInterfaceTwo{

}
