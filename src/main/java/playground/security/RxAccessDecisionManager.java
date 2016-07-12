package playground.security;

import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.core.Authentication;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface RxAccessDecisionManager {
	public Mono<Boolean> decide(Authentication authentication, Object object, Flux<ConfigAttribute> configAttributes);
}
