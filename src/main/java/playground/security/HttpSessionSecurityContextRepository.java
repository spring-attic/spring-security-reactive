package playground.security;

import org.springframework.security.core.context.SecurityContext;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

public class HttpSessionSecurityContextRepository {
	final String SESSION_ATTR = "USER";

	public Mono<Void> save(ServerWebExchange exchange, SecurityContext context) {
		return exchange.getSession().map(session -> {
			session.getAttributes().put(SESSION_ATTR, context);
			return session;
		}).after();
	}

	public Mono<SecurityContext> load(ServerWebExchange exchange) {
		return exchange.getSession().then( session -> {
			SecurityContext context = (SecurityContext) session.getAttributes().get(SESSION_ATTR);
			return context == null ? Mono.empty() : Mono.just(context);
		});
	}
}