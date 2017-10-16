package net.zdsoft.cache.utils;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author shenke
 * @since 2017.09.11
 */
public class BeanUtils {

    public static final Set<Class<?>> NATIVE_TYPE = new HashSet<Class<?>>(){{
        add(int.class);
        add(Integer.class);
        add(float.class);
        add(Float.class);
        add(char.class);
        add(Character.class);
        add(double.class);
        add(Double.class);
        add(String.class);
        add(boolean.class);
        add(Boolean.class);
    }};

    public static final Set<Class<?>> NATIVE_COLLECTION_TYPE = new HashSet<Class<?>>(){{
        add(Set.class);
        add(List.class);
        add(ArrayList.class);
        add(HashSet.class);
        add(Map.class);
    }};

    public static <T, O> List<T> getId(List<O> os) {
        ExpressionParser parser = new SpelExpressionParser();
        EvaluationContext context = new StandardEvaluationContext();
        context.setVariable("list", os);
        return parser.parseExpression("#list.![#this.id]").getValue(context, List.class);
    }
    
    public static Object getId(Object obj) {
        try {
            ExpressionParser parser = new SpelExpressionParser();
            EvaluationContext context = new StandardEvaluationContext();
            context.setVariable("obj", obj);
            return parser.parseExpression("#obj.id").getValue(context, String.class);
        } catch (Exception e) {
            return null;
        }
    }

    public static <T> T getGeneric(Class<T> tClass) {
        if ( tClass == null ) {
            throw new IllegalArgumentException("tClass is null");
        }
        try {
            return tClass.newInstance();
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    /**
     * FIXME error
     * @param eClass
     * @param number
     * @param <O>
     * @param <E>
     * @return
     */
    public static  <O,E> Class<O> getGenericType(Class<E> eClass, int number) {

        try {
            Type type = eClass.getGenericSuperclass();
            if ( Object.class.equals(type) ) {
                Type[] types = eClass.getGenericInterfaces();
                if ( types != null ) {
                    for (Type type1 : types) {
                        if ( type1 instanceof ParameterizedType ) {
                            Type[] argumentTypes = ((ParameterizedType)type1).getActualTypeArguments();
                            if ( number >= argumentTypes.length ){
                                break;
                            } else {
                                if ( argumentTypes[number-1] instanceof TypeVariable ) {
                                    return null;
                                }
                                return (Class<O>) argumentTypes[number-1];
                            }
                        }
                    }
                }
                return null;
            }
            Type[] types = ((ParameterizedType)type).getActualTypeArguments();
            if ( ArrayUtils.isEmpty(types) ) {
                return null;
            }
            if ( number > types.length + 1 ) {
                return (Class<O>) types[types.length-1];
            }
            return (Class<O>) types[number-1];
        } catch (Exception e) {

        }
        return null;
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

    public static Class<?> getRowType(Type type) {
        if ( type instanceof ParameterizedType) {
            return (Class<?>) ((ParameterizedType)type).getRawType();
        }
        return (Class<?>) type;
    }

    public static List<Type> getActualTypeArguments(Type type) {
        List<Type> typeArguments = new ArrayList<Type>();
        if ( type instanceof ParameterizedType) {
            Type[] types = ((ParameterizedType)type).getActualTypeArguments();
            if ( types != null ) {
                for (Type t : types) {
                    typeArguments.add(t);
                }
            }
        }
        return typeArguments;
    }

    public static final String getArrayHash(Object obj) {
        if ( obj == null ) {
            return "null";
        }
        int hashCode = 0;
        if ( obj.getClass().isArray() ) {
            for (Object o : (Object[]) obj) {
                if ( o == null ){
                    continue;
                }
                hashCode += o.hashCode();
            }
        }
        if ( obj instanceof Collection ) {
            for (Object o : (Collection) obj) {
                if ( o == null ){
                    continue;
                }
                hashCode += o.hashCode();
            }
        }
        return String.valueOf(hashCode);
    }
}
