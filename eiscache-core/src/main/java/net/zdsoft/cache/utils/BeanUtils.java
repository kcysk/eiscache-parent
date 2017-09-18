package net.zdsoft.cache.utils;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Set;

/**
 * @author shenke
 * @since 2017.09.11
 */
public class BeanUtils {

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

    public static  <O,E> Class<O> getGenericType(Class<E> eClass, int number) {
        Type type = eClass.getGenericSuperclass();
        Type[] types = ((ParameterizedType)type).getActualTypeArguments();
        if ( ArrayUtils.isEmpty(types) ) {
            return null;
        }
        if ( number > types.length + 1 ) {
            return (Class<O>) types[types.length-1];
        }
        return (Class<O>) types[number-1];
    }

    public static <O,E> Class<O> getFirstGenericType(Class<E> eClass) {
        return getGenericType(eClass, 1);
    }

    public static Method getSameMethod(Class<?> targetClass, Method originMethod) {
        try {
            return targetClass.getMethod(originMethod.getName(), originMethod.getParameterTypes());
        } catch (NoSuchMethodException e) {
            return null;
        }
    }
}
