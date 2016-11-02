package org.springframework.security.web.server.context;

import java.security.Principal;

import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.ServerWebExchangeDecorator;

import reactor.core.publisher.Mono;

public final class SecurityContextRepositoryServerWebExchange extends ServerWebExchangeDecorator {
	private final SecurityContextRepository repository;

	public SecurityContextRepositoryServerWebExchange(ServerWebExchange delegate, SecurityContextRepository repository) {
		super(delegate);
		this.repository = repository;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T extends Principal> Mono<T> getPrincipal() {
		return this.repository.load(getDelegate())
			.filter( c -> c.getAuthentication() != null)
			.then( c -> {
				return Mono.just( (T) c.getAuthentication());
			});
	}
}
