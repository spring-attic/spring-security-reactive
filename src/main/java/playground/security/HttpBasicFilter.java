package playground.security;

import java.util.Base64;

import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import org.springframework.web.server.WebSession;

import playground.Person;
import reactor.core.publisher.EmitterProcessor;
import reactor.core.publisher.FluxProcessor;
import reactor.core.publisher.Mono;

public class HttpBasicFilter implements WebFilter {

	HttpSessionSecurityContextRepository securityContextRepository = new HttpSessionSecurityContextRepository();


	UserDetailsRepository userDetailsRepository = new UserDetailsRepository();

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

			UserDetails user = userDetailsRepository.findByUsername(username).get();

			if(user.getPassword().equals(password)) {
				SecurityContext context = new SecurityContextImpl();
				UserDetails ud = new User(username, password, AuthorityUtils.createAuthorityList("ROLE_USER"));
				context.setAuthentication(new UsernamePasswordAuthenticationToken(ud, password, ud.getAuthorities()));
				return securityContextRepository
					.save(exchange, context)
					.after( () ->{
						return chain.filter(exchange);
					});
			}
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
}