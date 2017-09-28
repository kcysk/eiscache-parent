package net.zdsoft.cache.utils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * @author shenke
 * @since 2017.09.21
 */
public class TypeBuilder {
    private ParameterizedType type;
    public static TypeBuilder build() {
        return new TypeBuilder();
    }

    public ParameterizedType returnType() {
        if ( type != null ) {
            return type;
        }
        type = new ParameterizedType() {
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
                StringBuilder ts = new StringBuilder();
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
        return type;
    }

    public TypeBuilder buildRowType(Type rowType) {
        clearActualType();
        this.rawType = rowType;
        return this;
    }

    /**
     * 一般情况下用不到
     * @param ownerType
     * @return
     */
    public TypeBuilder buildOwnerType(Type ownerType) {
        clearActualType();
        this.ownerType = ownerType;
        return this;
    }

    public TypeBuilder buildArgumentType(Type ... actualTypeArguments) {
        clearActualType();
        this.actualTypeArguments = actualTypeArguments;
        return this;
    }

    private void clearActualType() {
        this.type = null;
    }

    private Type[] actualTypeArguments;
    private Type   rawType;
    private Type   ownerType;
}
