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
package net.zdsoft.cache.expression;

import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;

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

}