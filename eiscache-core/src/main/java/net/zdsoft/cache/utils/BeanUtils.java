/*
 * Copyright 2012-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.zdsoft.cache.utils;

import org.apache.log4j.Logger;
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

    private static Logger logger = Logger.getLogger(BeanUtils.class);

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
     *
     * @param eClass
     * @param number
     * @param <E>
     * @return
     */
    public static  <E> Type getGenericType(Class<E> eClass, int number) {
        Type superType = eClass.getGenericSuperclass();
        //超类
        if ( !Object.class.equals(superType) ) {
            if ( superType instanceof ParameterizedType ) {
                Type[] actualTypeArguments = ((ParameterizedType) superType).getActualTypeArguments();
                Type genericType = actualTypeArguments[number - 1];
                if ( genericType instanceof TypeVariable ) {
                    logger.error(eClass.getName() + " generic type is TypeVariable ");
                    return null;
                }
                return genericType;
            }
        }
        //接口
        else {
            Type[] interfaceTypes = eClass.getGenericInterfaces();
            for ( Type interfaceType : interfaceTypes ) {
                if ( interfaceType instanceof ParameterizedType ) {
                    Type[] interfaceAcualArgumentsTypes = ((ParameterizedType)interfaceType).getActualTypeArguments();
                    if ( interfaceAcualArgumentsTypes.length >= number ) {
                        Type genericType = interfaceAcualArgumentsTypes[number - 1];
                        if ( genericType instanceof TypeVariable ) {
                            logger.error(eClass.getName() + " generic type is TypeVariable ");
                            return null;
                        }
                        return genericType;
                    } else {
                        continue;
                    }
                }
            }
        }
        return null;
    }

    public static <E> Type getFirstGenericType(Class<E> eClass) {

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
                if ( o instanceof Integer ) {
                    o = o.toString();
                }
                hashCode += o.hashCode();
            }
        }
        if ( obj instanceof Collection ) {
            for (Object o : (Collection) obj) {
                if ( o == null ){
                    continue;
                }
                if ( o instanceof Integer) {
                    o = o.toString();
                }
                hashCode += o.hashCode();
            }
        }
        return String.valueOf(hashCode);
    }
}
