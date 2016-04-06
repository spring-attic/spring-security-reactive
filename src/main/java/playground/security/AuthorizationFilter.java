package playground.security;

import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import reactor.core.publisher.Mono;

public class AuthorizationFilter implements WebFilter {
	HttpSessionSecurityContextRepository securityContextRepository = new HttpSessionSecurityContextRepository();

	AuthenticationEntryPoint entryPoint = new HttpBasicAuthenticationEntryPoint();

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
		return securityContextRepository.load(exchange)
			.where(c -> {
				Authentication authentication = c.getAuthentication();
				return authentication != null && authentication.isAuthenticated();
			})
			.otherwiseIfEmpty(Mono.defer(() -> {
				return entryPoint.commence(exchange, new AuthenticationCredentialsNotFoundException("Not Found"));
			}))
			.then(sc -> {
				return chain.filter(exchange);
			});
	}

}
