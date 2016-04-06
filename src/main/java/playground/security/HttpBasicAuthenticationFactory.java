package playground.security;

import java.util.Base64;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

public class HttpBasicAuthenticationFactory implements AuthenticationFactory<ServerWebExchange> {

	@Override
	public Mono<Authentication> createToken(ServerWebExchange exchange) {
		ServerHttpRequest request = exchange.getRequest();
		String authorization = request.getHeaders().getFirst("Authorization");
		if(authorization == null) {
			return Mono.empty();
		}

		String credentials = authorization.substring("Basic ".length(), authorization.length());
		byte[] decodedCredentials = Base64.getDecoder().decode(credentials);
		String decodedAuthz = new String(decodedCredentials);
		String[] userParts = decodedAuthz.split(":");

		if(userParts.length != 2) {
			return Mono.empty();
		}

		String username = userParts[0];
		String password = userParts[1];

		return Mono.just(new UsernamePasswordAuthenticationToken(username, password));
	}
}
