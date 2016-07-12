package playground.security;

import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Flux;

public class ServerWebExchangeMetadataSource {

	public Flux<ConfigAttribute> getConfigAttributes(ServerWebExchange exchange) {
		String path = exchange.getRequest().getURI().getPath();
		if(path.contains("admin")) {
			return Flux.just(new SecurityConfig("ROLE_ADMIN"));
		}
		return Flux.just(new SecurityConfig("ROLE_USER"));
	}
}
