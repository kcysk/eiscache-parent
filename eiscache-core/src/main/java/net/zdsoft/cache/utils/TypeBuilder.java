package net.zdsoft.cache.utils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * @author shenke
 * @since 2017.09.21
 */
public class TypeBuilder {
    public static TypeBuilder build() {
        return new TypeBuilder();
    }

    public ParameterizedType returnType() {
        return new ParameterizedType() {
            @Override
            public Type[] getActualTypeArguments() {
                return TypeBuilder.this.actualTypeArguments;
            }

            @Override
            public Type getRawType() {
                return TypeBuilder.this.rawType;
            }

            @Override
            public Type getOwnerType() {
                return TypeBuilder.this.ownerType;
            }

            @Override
            public String toString() {
                return "rowType is " + typeToString(TypeBuilder.this.rawType)
                        + "actualTypeArguments is " + typeToString(TypeBuilder.this.actualTypeArguments)
                        + "ownerType is " + typeToString(TypeBuilder.this.ownerType);
            }

            private String typeToString(Type ... type) {
                StringBuffer ts = new StringBuffer();
                if ( type != null ) {
                    for (Type t : type) {
                        if ( t != null ){
                            ts.append(((Class<?>)t).getName());
                        } else {
                            ts.append("null");
                        }
                        ts.append(";");
                    }
                } else {
                    ts.append("null");
                }
                return ts.toString();
            }
        };
    }

    public TypeBuilder buildRowType(Type rowType) {
        this.rawType = rowType;
        return this;
    }

    /**
     * 一般情况下用不到
     * @param ownerType
     * @return
     */
    public TypeBuilder buildOwnerType(Type ownerType) {
        this.ownerType = ownerType;
        return this;
    }

    public TypeBuilder buildArgumentType(Type ... actualTypeArguments) {
        this.actualTypeArguments = actualTypeArguments;
        return this;
    }

    private Type[] actualTypeArguments;
    private Type   rawType;
    private Type   ownerType;
}
