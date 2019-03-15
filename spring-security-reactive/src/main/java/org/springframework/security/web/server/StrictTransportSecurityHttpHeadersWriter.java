/*
 * Copyright 2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.security.web.server;

import java.time.Duration;

import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

/**
 * @author Rob Winch
 * @since 5.0
 */
public final class StrictTransportSecurityHttpHeadersWriter implements HttpHeadersWriter {
	public static final String STRICT_TRANSPORT_SECURITY = "Strict-Transport-Security";

	private String maxAge;

	private String subdomain;

	private HttpHeadersWriter delegate;

	/**
	 *
	 */
	public StrictTransportSecurityHttpHeadersWriter() {
		setIncludeSubDomains(true);
		setMaxAge(Duration.ofDays(365L));
		updateDelegate();
	}

	/* (non-Javadoc)
	 * @see org.springframework.security.web.server.HttpHeadersWriter#writeHttpHeaders(org.springframework.http.HttpHeaders)
	 */
	@Override
	public Mono<Void> writeHttpHeaders(ServerWebExchange exchange) {
		return isSecure(exchange) ? delegate.writeHttpHeaders(exchange) : Mono.empty();
	}

	public void setIncludeSubDomains(boolean includeSubDomains) {
		subdomain = includeSubDomains ? " ; includeSubDomains" : "";
		updateDelegate();
	}

	public void setMaxAge(Duration maxAge) {
		this.maxAge = "max-age=" + maxAge.getSeconds();
		updateDelegate();
	}

	private void updateDelegate() {
		delegate = StaticHttpHeadersWriter.builder()
				.header(STRICT_TRANSPORT_SECURITY, maxAge + subdomain)
				.build();
	}

	private boolean isSecure(ServerWebExchange exchange) {
		String scheme = exchange.getRequest().getURI().getScheme();
		boolean isSecure = scheme != null && scheme.equalsIgnoreCase("https");
		return isSecure;
	}
}
