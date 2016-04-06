package playground.security;

import org.springframework.security.core.Authentication;

import reactor.core.publisher.Mono;

public interface AuthenticationFactory<T> {

	Mono<Authentication> createToken(T t);
}
