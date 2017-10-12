package net.zdsoft.cache.aop;

import org.springframework.aop.ClassFilter;

/**
 * @author shenke
 * @since 2017.09.21
 */
public class ClassFilterAdapter implements ClassFilter {

    private DynamicCacheClassFilter filter;

    public ClassFilterAdapter(DynamicCacheClassFilter filter) {
        this.filter = filter;
    }

    @Override
    public boolean matches(Class<?> aClass) {
        if ( filter == null ) {
            return false;
        }
        boolean match = filter.matches(aClass);
        return match;
    }
}
