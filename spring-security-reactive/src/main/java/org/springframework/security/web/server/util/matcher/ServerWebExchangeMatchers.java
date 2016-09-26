package org.springframework.security.web.server.util.matcher;

import org.springframework.web.server.ServerWebExchange;

public abstract class ServerWebExchangeMatchers {

	public static ServerWebExchangeMatcher antMatchers(String pattern) {
		return new PathMatcherServerWebExchangeMatcher(pattern);
	}

	public static ServerWebExchangeMatcher anyExchange() {
		return new ServerWebExchangeMatcher() {
			@Override
			public boolean matches(ServerWebExchange exchange) {
				return true;
			}
		};
	}

	private ServerWebExchangeMatchers() {}
}
