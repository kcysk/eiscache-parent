package net.zdsoft.cache;

import net.zdsoft.cache.annotation.Cacheable;
import net.zdsoft.cache.service.impl.UserServiceImpl;

import java.lang.reflect.Method;

/**
 * @author shenke
 * @since 2017.10.10
 */
public class AnnotationTest {

    public static void main(String[] args){
        for (Method method : UserServiceImpl.class.getDeclaredMethods()) {
            Cacheable cacheable = method.getAnnotation(Cacheable.class);
            if ( cacheable != null ) {
                System.out.println(cacheable.key());
            }
        }
    }
}
