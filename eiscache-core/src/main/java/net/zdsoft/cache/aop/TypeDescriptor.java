package net.zdsoft.cache.aop;

import net.zdsoft.cache.utils.TypeBuilder;
import org.aopalliance.intercept.MethodInvocation;

/**
 * @author shenke
 * @since 2017.09.21
 */
public interface TypeDescriptor {

    /**
     * 解决因为泛型导致无法获取实际类型的问题 <br>
     * 该方法实现取决于基类方法的返回类型 <br>
     * 若基类方法的返回类型是Map<String,List<T>> 则该方法就应该构建这个Map的实际类型信息
     * @param invocation
     * @param targetClass 实际的对象类型信息，不要从invocation获取，可能是代理对象
     */
    TypeBuilder buildType(MethodInvocation invocation, Class<?> targetClass);

}
