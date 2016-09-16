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
