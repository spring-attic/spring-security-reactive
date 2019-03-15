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
package org.springframework.security.web.server.context;

import org.springframework.security.core.context.SecurityContext;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

/**
 *
 * @author Rob Winch
 * @since 5.0
 */
public class WebSessionSecurityContextRepository implements SecurityContextRepository {
	final String SESSION_ATTR = "USER";

	public Mono<Void> save(ServerWebExchange exchange, SecurityContext context) {
		return exchange.getSession().map(session -> {
			session.getAttributes().put(SESSION_ATTR, context);
			return session;
		}).then();
	}

	public Mono<SecurityContext> load(ServerWebExchange exchange) {
		return exchange.getSession().flatMap( session -> {
			SecurityContext context = (SecurityContext) session.getAttributes().get(SESSION_ATTR);
			return context == null ? Mono.empty() : Mono.just(context);
		});
	}
}