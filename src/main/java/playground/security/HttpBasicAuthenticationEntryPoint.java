package playground.security;

import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

public class HttpBasicAuthenticationEntryPoint implements AuthenticationEntryPoint {

	@Override
	public <T> Mono<T> commence(ServerWebExchange exchange, AuthenticationException e) {
		ServerHttpResponse response = exchange.getResponse();
		response.setStatusCode(HttpStatus.UNAUTHORIZED);
		response.getHeaders().set("WWW-Authenticate", "Basic realm=\"Reactive\"");
		return Mono.empty();
	}
}
