package playground.security;

import org.springframework.security.core.AuthenticationException;
import org.springframework.web.server.ServerWebExchange;


import reactor.core.publisher.Mono;

public interface AuthenticationEntryPoint {

	<T> Mono<T> commence(ServerWebExchange exchange, AuthenticationException e);
}
