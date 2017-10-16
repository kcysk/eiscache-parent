package net.zdsoft.cache.expression;

import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.expression.EvaluationException;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

/**
 * @author shenke
 * @since 2017.08.31
 */
public class CacheEvaluationContext extends MethodBasedEvaluationContext {

    public static final String VARIABLE_RESULT = "result";

    final Set<String> unavailable = new HashSet<String>();

    public CacheEvaluationContext(Object rootObject, Method method, Object[] arguments, ParameterNameDiscoverer parameterNameDiscoverer) {
        super(rootObject, method, arguments, parameterNameDiscoverer);
    }

    public void setUnavailable(String name) {
        this.unavailable.add(name);
    }

    @Override
    public Object lookupVariable(String name) {
        if ( unavailable.contains(name) ) {
            throw new EvaluationException("unable for " + name + "");
        }
        return super.lookupVariable(name);
    }
}
