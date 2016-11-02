package org.springframework.security.web.server.context;

import org.springframework.security.core.context.SecurityContext;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

public interface SecurityContextRepository {

	Mono<Void> save(ServerWebExchange exchange, SecurityContext context);

	Mono<SecurityContext> load(ServerWebExchange exchange);
}