package org.springframework.security.authentication;

import org.springframework.security.core.Authentication;

import reactor.core.publisher.Mono;

public class UserDetailsAuthenticationManager implements ReactiveAuthenticationManager {
	private final UserDetailsRepository repository;

	public UserDetailsAuthenticationManager(UserDetailsRepository repository) {
		this.repository = repository;
	}

	@Override
	public Mono<Authentication> authenticate(Authentication authentication) {
		final String username = authentication.getName();
		return repository
				.findByUsername(username)
				.filter( u -> u.getPassword().equals(authentication.getCredentials()))
				.map( u -> {
			return new UsernamePasswordAuthenticationToken(u, u.getPassword(), u.getAuthorities());
		});
	}
}
