package net.zdsoft.cache;

import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Set;

/**
 * @author shenke
 * @since 2017.09.11
 */
final public class BeanUtils {

    public static final <T, O> Set<T> getId(Collection<O> os) {
        ExpressionParser parser = new SpelExpressionParser();
        EvaluationContext context = new StandardEvaluationContext();
        context.setVariable("collection", os);
        return parser.parseExpression("#collection.![#this.id]").getValue(context, Set.class);
    }

    public static final Object getId(Object obj) {
        try {
            Field ID = obj.getClass().getField("id");
            if ( ID != null ) {
                ID.setAccessible(true);
                return ID.get(obj);
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }
}
