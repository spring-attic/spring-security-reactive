package playground.security;

import org.springframework.security.core.Authentication;

import reactor.core.publisher.Mono;

public interface RxAuthenticationManager {

	Mono<Authentication> authenticate(Authentication authentication);
}
