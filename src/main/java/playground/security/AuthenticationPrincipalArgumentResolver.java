package playground.security;

import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.web.reactive.method.HandlerMethodArgumentResolver;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

public class AuthenticationPrincipalArgumentResolver implements HandlerMethodArgumentResolver {
	HttpSessionSecurityContextRepository repository = new HttpSessionSecurityContextRepository();

	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		return parameter.hasParameterAnnotation(AuthenticationPrincipal.class);
	}

	@Override
	public Mono<Object> resolveArgument(MethodParameter parameter, ServerWebExchange exchange) {
		SecurityContext context = repository.load(exchange);
		Authentication authentication = context == null ? null : context.getAuthentication();
		return Mono.just(authentication == null ? null : authentication.getPrincipal());
	}

}
