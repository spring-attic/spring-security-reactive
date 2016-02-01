package playground.security;

import java.util.Base64;

import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import org.springframework.web.server.WebSession;

import reactor.core.publisher.Mono;

public class HttpBasicFilter implements WebFilter {

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
		ServerHttpRequest request = exchange.getRequest();
		ServerHttpResponse response = exchange.getResponse();
		String authorization = request.getHeaders().getFirst("Authorization");
		if(authorization != null) {
			String credentials = authorization.substring("Basic ".length(), authorization.length());
			byte[] decodedCredentials = Base64.getDecoder().decode(credentials);
			String decodedAuthz = new String(decodedCredentials);
			String[] userPassword = decodedAuthz.split(":");

			if(userPassword.length == 2 && userPassword[0].equals(userPassword[1])) {
				WebSession webSession = exchange.getSession().get();
				webSession.getAttributes().put("USER", userPassword[0]);
//				webSession.save();
				return chain.filter(exchange);
			}
		}
		WebSession webSession = exchange.getSession().get();
		if(webSession.isStarted()) {
			String username = (String) webSession.getAttributes().get("USER");
			if(username != null) {
				return chain.filter(exchange);
			}
		}
		response.setStatusCode(HttpStatus.UNAUTHORIZED);
		response.getHeaders().set("WWW-Authenticate", "Basic realm=\"Reactive\"");
		return Mono.empty();
	}
}