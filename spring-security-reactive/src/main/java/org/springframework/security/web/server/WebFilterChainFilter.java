/*
 * Copyright 2002-2016 the original author or authors.
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

import java.util.Iterator;
import java.util.List;

import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import reactor.core.publisher.Mono;

public class WebFilterChainFilter implements WebFilter {
	private final List<WebFilter> filters;

	public WebFilterChainFilter(List<WebFilter> filters) {
		super();
		this.filters = filters;
	}

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
		SecurityWebFilterChain delegate = new SecurityWebFilterChain(chain, filters.iterator());
		return delegate.filter(exchange);
	}

	static class SecurityWebFilterChain implements WebFilterChain {
		private final WebFilterChain delegate;
		private final Iterator<WebFilter> filters;

		public SecurityWebFilterChain(WebFilterChain delegate, Iterator<WebFilter> filters) {
			super();
			this.delegate = delegate;
			this.filters = filters;
		}

		@Override
		public Mono<Void> filter(ServerWebExchange exchange) {
			if (filters.hasNext()) {
				WebFilter filter = filters.next();
				return filter.filter(exchange, this);
			} else {
				return delegate.filter(exchange);
			}
		}

	}
}
