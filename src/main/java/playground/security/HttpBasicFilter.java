package playground.security;

import java.util.Arrays;

import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import reactor.core.publisher.Mono;

public class HttpBasicFilter implements WebFilter {

	AuthenticationFactory<ServerWebExchange> tokenFactory = new HttpBasicAuthenticationFactory();

	HttpSessionSecurityContextRepository securityContextRepository = new HttpSessionSecurityContextRepository();

	RxAuthenticationManager authenticationManager = createAuthenticationManager();

	AuthenticationEntryPoint entryPoint = new HttpBasicAuthenticationEntryPoint();

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

	private RxAuthenticationManager createAuthenticationManager() {
		User rob = new User("rob","rob",AuthorityUtils.createAuthorityList("ROLE_USER"));
		InMemoryUserDetailsManager userDetailsService = new InMemoryUserDetailsManager(Arrays.asList(rob));
		DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
		authenticationProvider.setUserDetailsService(userDetailsService);
		ProviderManager authenticationManager = new ProviderManager(Arrays.asList(authenticationProvider));
		return new RxAuthenticationManagerAdapter(authenticationManager);
	}
}