package net.zdsoft.cache.expression;

import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.PrioritizedParameterNameDiscoverer;
import org.springframework.core.StandardReflectionParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.util.ClassUtils;

/**
 * @author shenke
 * @since 2017.08.31
 */
public class CacheExpressionEvaluator {

    public static final Object UN_AVAILABLE = new Object();
    public static final Object NO_RESULT = new Object();

    private ExpressionParser expressionParser;

    private DefaultParameterNameDiscoverer parameterNameDiscoverer = new DefaultParameterNameDiscoverer();

    public CacheExpressionEvaluator(ExpressionParser expressionParser) {
        this.expressionParser = expressionParser;
    }

    public DefaultParameterNameDiscoverer getParameterNameDiscoverer() {
        return parameterNameDiscoverer;
    }

    public Expression getExpression(String expression) {
        return this.expressionParser.parseExpression(expression);
    }

    public Object getValue(String expression, EvaluationContext context) {
        return getExpression(expression).getValue(context);
    }

    public <T> T getValue(String expression, EvaluationContext context, Class<T> type) {
        return getExpression(expression).getValue(context, type);
    }

    static class DefaultParameterNameDiscoverer extends PrioritizedParameterNameDiscoverer {

        private static final boolean standardReflectionAvailable = ClassUtils.isPresent(
                "java.lang.reflect.Executable", DefaultParameterNameDiscoverer.class.getClassLoader());


        public DefaultParameterNameDiscoverer() {
            //if (standardReflectionAvailable) {
            //    addDiscoverer(new StandardReflectionParameterNameDiscoverer());
            //}
            addDiscoverer(new LocalVariableTableParameterNameDiscoverer());
        }
    }
}