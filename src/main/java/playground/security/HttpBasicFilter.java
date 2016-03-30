package playground.security;

import java.util.Arrays;
import java.util.Base64;

import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
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

	HttpSessionSecurityContextRepository securityContextRepository = new HttpSessionSecurityContextRepository();


	RxAuthenticationManager authenticationManager = createAuthenticationManager();

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
		ServerHttpRequest request = exchange.getRequest();
		ServerHttpResponse response = exchange.getResponse();
		String authorization = request.getHeaders().getFirst("Authorization");
		if(authorization != null) {
			String credentials = authorization.substring("Basic ".length(), authorization.length());
			byte[] decodedCredentials = Base64.getDecoder().decode(credentials);
			String decodedAuthz = new String(decodedCredentials);
			String[] userParts = decodedAuthz.split(":");

			if(userParts.length != 2) {
				return withSession(exchange, chain);
			}

			String username = userParts[0];
			String password = userParts[1];

			UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username, password);

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
					.doOnError( e -> {
						response.setStatusCode(HttpStatus.UNAUTHORIZED);
						response.getHeaders().set("WWW-Authenticate", "Basic realm=\"Reactive\"");
					});
		}

		return withSession(exchange, chain);
	}

	private Mono<Void> withSession(ServerWebExchange exchange, WebFilterChain chain) {

		ServerHttpResponse response = exchange.getResponse();
		Mono<SecurityContext> context = securityContextRepository.load(exchange);

		return context
			.where(c -> {
				Authentication authentication = c.getAuthentication();
				return authentication != null && authentication.isAuthenticated();
			})
			.otherwiseIfEmpty(Mono.defer(() -> {
				response.setStatusCode(HttpStatus.UNAUTHORIZED);
				response.getHeaders().set("WWW-Authenticate", "Basic realm=\"Reactive\"");
				return Mono.empty();
			}))
			.then(sc -> {
				return chain.filter(exchange);
			});
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