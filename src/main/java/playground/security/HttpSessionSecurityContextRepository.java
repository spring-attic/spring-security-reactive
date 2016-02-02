package playground.security;

import org.springframework.security.core.context.SecurityContext;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebSession;

public class HttpSessionSecurityContextRepository {
	final String SESSION_ATTR = "USER";

	public void save(ServerWebExchange exchange, SecurityContext context) {
		WebSession webSession = exchange.getSession().get();
		webSession.getAttributes().put(SESSION_ATTR, context);
	}

	public SecurityContext load(ServerWebExchange exchange) {
		WebSession webSession = exchange.getSession().get();
		if(!webSession.isStarted()) {
			return null;
		}
		return (SecurityContext) webSession.getAttributes().get(SESSION_ATTR);
	}
}
