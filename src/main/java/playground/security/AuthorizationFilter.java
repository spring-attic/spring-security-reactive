package playground.security;

import java.util.function.Function;

import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class AuthorizationFilter implements WebFilter {
	HttpSessionSecurityContextRepository securityContextRepository = new HttpSessionSecurityContextRepository();

	AuthenticationEntryPoint entryPoint = new HttpBasicAuthenticationEntryPoint();

	ServerWebExchangeMetadataSource source = new ServerWebExchangeMetadataSource();

	RxAccessDecisionManagerAdapter accessDecisionManager;

	public AuthorizationFilter(RxAccessDecisionManagerAdapter accessDecisionManager) {
		super();
		this.accessDecisionManager = accessDecisionManager;
	}

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
		return securityContextRepository.load(exchange)
			.map(SecurityContext::getAuthentication)
			.filter(authentication -> {
				return authentication != null && authentication.isAuthenticated();
			})
			.then(authentication -> {
				return source.getConfigAttributes(exchange).as( (Function<? super Flux<ConfigAttribute>, Mono<Boolean>>) a -> {
					return accessDecisionManager.decide(authentication, exchange, a);
				});
			})
			.filter(t -> t)
			.otherwiseIfEmpty(Mono.defer(() -> {
				return entryPoint.commence(exchange, new AuthenticationCredentialsNotFoundException("Not Found"));
			}))
			.then(sc -> {
				return chain.filter(exchange);
			});
	}

}
