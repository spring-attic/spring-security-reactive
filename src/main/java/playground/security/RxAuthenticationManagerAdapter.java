package playground.security;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;

import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

public class RxAuthenticationManagerAdapter implements RxAuthenticationManager {
	private final AuthenticationManager authenticationManager;

	public RxAuthenticationManagerAdapter(AuthenticationManager authenticationManager) {
		this.authenticationManager = authenticationManager;
	}

	@Override
	public Mono<Authentication> authenticate(Authentication token) {
		return Mono.<Authentication>just(token)
			.publishOn(Schedulers.computation())
			.then( t -> {
				try {
					return Mono.just(authenticationManager.authenticate(token));
				} catch(Throwable error) {
					return Mono.error(error);
				}
			});
	}
}