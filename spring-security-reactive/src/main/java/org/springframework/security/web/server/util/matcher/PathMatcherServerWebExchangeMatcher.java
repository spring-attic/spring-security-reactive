package org.springframework.security.web.server.util.matcher;

import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.Assert;
import org.springframework.util.PathMatcher;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.util.HttpRequestPathHelper;

public final class PathMatcherServerWebExchangeMatcher implements ServerWebExchangeMatcher {
	private HttpRequestPathHelper helper = new HttpRequestPathHelper();

	private PathMatcher pathMatcher = new AntPathMatcher();

	private final String pattern;
	private final HttpMethod method;

	public PathMatcherServerWebExchangeMatcher(String pattern) {
		this(pattern, null);
	}

	public PathMatcherServerWebExchangeMatcher(String pattern, HttpMethod method) {
		Assert.notNull(pattern, "pattern cannot be null");
		this.pattern = pattern;
		this.method = method;
	}

	@Override
	public boolean matches(ServerWebExchange exchange) {
		ServerHttpRequest request = exchange.getRequest();
		if(this.method != null && !this.method.equals(request.getMethod())) {
			return false;
		}
		String path = helper.getLookupPathForRequest(exchange);
		return pathMatcher.match(pattern, path);
	}

	public void setPathMatcher(PathMatcher pathMatcher) {
		Assert.notNull(pathMatcher, "pathMatcher cannot be null");
		this.pathMatcher = pathMatcher;
	}
}