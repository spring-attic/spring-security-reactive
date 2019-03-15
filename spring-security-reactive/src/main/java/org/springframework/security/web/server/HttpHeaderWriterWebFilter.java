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

import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import reactor.core.publisher.Mono;

/**
 * Invokes a {@link HttpHeadersWriter} on
 * {@link ServerHttpResponse#beforeCommit(java.util.function.Supplier)}.
 *
 * @author Rob Winch
 * @since 5.0
 */
public class HttpHeaderWriterWebFilter implements WebFilter {
	private final HttpHeadersWriter writer;

	public HttpHeaderWriterWebFilter(HttpHeadersWriter writer) {
		super();
		this.writer = writer;
	}

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
		exchange.getResponse().beforeCommit(() -> {
			return writer.writeHttpHeaders(exchange);
		});
		return chain.filter(exchange);
	}

}
