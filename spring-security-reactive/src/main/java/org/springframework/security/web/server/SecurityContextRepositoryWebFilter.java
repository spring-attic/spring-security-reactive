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

import org.springframework.core.annotation.Order;
import org.springframework.security.web.server.context.SecurityContextRepository;
import org.springframework.security.web.server.context.SecurityContextRepositoryServerWebExchange;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import reactor.core.publisher.Mono;

@Order(-10)
public class SecurityContextRepositoryWebFilter implements WebFilter {
	private final SecurityContextRepository repository;

	public SecurityContextRepositoryWebFilter(SecurityContextRepository repository) {
		this.repository = repository;
	}

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
		SecurityContextRepositoryServerWebExchange delegate =
				new SecurityContextRepositoryServerWebExchange(exchange, repository);
		return chain.filter(delegate);
	}
}