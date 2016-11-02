package org.springframework.security.web.server;

import org.springframework.core.annotation.Order;
import org.springframework.security.web.server.context.SecurityContextRepository;
import org.springframework.security.web.server.context.SecurityContextRepositoryServerWebExchange;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import reactor.core.publisher.Mono;

@Order(-10)
public class SecurityContextRepositoryWebFilter implements WebFilter {
	private final SecurityContextRepository repository;

	public SecurityContextRepositoryWebFilter(SecurityContextRepository repository) {
		this.repository = repository;
	}

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
		SecurityContextRepositoryServerWebExchange delegate =
				new SecurityContextRepositoryServerWebExchange(exchange, repository);
		return chain.filter(delegate);
	}
}