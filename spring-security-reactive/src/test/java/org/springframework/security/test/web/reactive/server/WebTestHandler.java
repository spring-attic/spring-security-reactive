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
package org.springframework.security.test.web.reactive.server;

import org.springframework.mock.http.server.reactive.MockServerHttpRequest.BaseBuilder;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebHandler;
import org.springframework.web.server.handler.FilteringWebHandler;

import reactor.core.publisher.Mono;

import java.util.Arrays;

/**
 *
 * @author Rob Winch
 *
 */
public class WebTestHandler {
	private final WebHandler handler;

	private WebTestHandler(WebHandler handler) {
		this.handler = handler;
	}

	public WebHandlerResult exchange(BaseBuilder<?> baseBuilder) {
		ServerWebExchange exchange = baseBuilder.toExchange();
		handler.handle(exchange);
		return new WebHandlerResult(exchange);
	}

	public static class WebHandlerResult {
		private final ServerWebExchange exchange;

		private WebHandlerResult(ServerWebExchange exchange) {
			this.exchange = exchange;
		}

		public ServerWebExchange getExchange() {
			return exchange;
		}
	}

	public static WebTestHandler bindToWebFilters(WebFilter... filters) {
		return new WebTestHandler(new FilteringWebHandler(exchange -> Mono.empty(), Arrays.asList(filters)));
	}
}
