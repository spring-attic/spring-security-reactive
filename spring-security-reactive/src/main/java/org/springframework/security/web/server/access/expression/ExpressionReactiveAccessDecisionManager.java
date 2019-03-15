/*
 * Copyright 2002-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.security.web.server.access.expression;

import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.ReactiveAccessDecisionManager;
import org.springframework.security.access.expression.ExpressionUtils;
import org.springframework.security.access.expression.SecurityExpressionHandler;
import org.springframework.security.core.Authentication;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class ExpressionReactiveAccessDecisionManager implements ReactiveAccessDecisionManager<ServerWebExchange> {
	SecurityExpressionHandler<ServerWebExchange> handler = new DefaultServerWebExchangeExpressionHandler();

	@Override
	public Mono<Boolean> decide(Authentication authentication, ServerWebExchange object, Flux<ConfigAttribute> configAttributes) {
		ConfigAttribute attribute = configAttributes.blockFirst();
		EvaluationContext context = handler.createEvaluationContext(authentication, object);
		Expression expression = handler.getExpressionParser().parseExpression(attribute.getAttribute());
		return Mono.just(ExpressionUtils.evaluateAsBoolean(expression, context));
	}
}
