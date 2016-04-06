package playground.security;

import org.springframework.core.annotation.Order;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import reactor.core.publisher.Mono;

@Order(0)
public class AuthenticationFilter implements WebFilter {

	private HttpSessionSecurityContextRepository securityContextRepository;

	private AuthenticationFactory<ServerWebExchange> tokenFactory;

	private RxAuthenticationManager authenticationManager;

	private AuthenticationEntryPoint entryPoint;

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
		return tokenFactory.createToken(exchange)
			.then( token -> {
				return authenticationManager.authenticate(token)
					.then(authentication -> {
						SecurityContext context = new SecurityContextImpl();
						context.setAuthentication(authentication);
						return securityContextRepository
							.save(exchange, context)
							.after( () ->{
								return chain.filter(exchange);
							});
					})
					.otherwise( t -> {
						if(t instanceof AuthenticationException) {
							return entryPoint.commence(exchange, (AuthenticationException) t);
						}
						return Mono.error(t);
					});
			})
			.otherwiseIfEmpty(Mono.defer(() -> {
				return chain.filter(exchange);
			}));
	}

	public void setSecurityContextRepository(HttpSessionSecurityContextRepository securityContextRepository) {
		this.securityContextRepository = securityContextRepository;
	}

	public void setTokenFactory(AuthenticationFactory<ServerWebExchange> tokenFactory) {
		this.tokenFactory = tokenFactory;
	}

	public void setAuthenticationManager(RxAuthenticationManager authenticationManager) {
		this.authenticationManager = authenticationManager;
	}

	public void setEntryPoint(AuthenticationEntryPoint entryPoint) {
		this.entryPoint = entryPoint;
	}
}