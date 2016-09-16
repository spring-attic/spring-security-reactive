/*
 * Copyright 2002-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.security.web.server;


import java.util.function.Function;

import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.ReactiveAccessDecisionManager;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.web.server.access.expression.ServerWebExchangeMetadataSource;
import org.springframework.security.web.server.authentication.www.HttpBasicAuthenticationEntryPoint;
import org.springframework.security.web.server.context.WebSessionSecurityContextRepository;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 *
 * @author Rob Winch
 * @since 5.0
 */
public class AuthorizationWebFilter implements WebFilter {
	WebSessionSecurityContextRepository securityContextRepository = new WebSessionSecurityContextRepository();

	AuthenticationEntryPoint entryPoint = new HttpBasicAuthenticationEntryPoint();

	ServerWebExchangeMetadataSource source;

	ReactiveAccessDecisionManager<? super ServerWebExchange> accessDecisionManager;

	public AuthorizationWebFilter(ReactiveAccessDecisionManager<? super ServerWebExchange> accessDecisionManager, ServerWebExchangeMetadataSource source) {
		super();
		this.accessDecisionManager = accessDecisionManager;
		this.source = source;
	}

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
		return securityContextRepository.load(exchange)
			.map(SecurityContext::getAuthentication)
			.filter(authentication -> {
				return authentication != null && authentication.isAuthenticated();
			})
			.then(authentication -> {
				return source.getConfigAttributes(exchange).as( (Function<? super Flux<ConfigAttribute>, Mono<Boolean>>) a -> {
					return accessDecisionManager.decide(authentication, exchange, a);
				});
			})
			.filter(t -> t)
			.otherwiseIfEmpty(Mono.defer(() -> {
				return entryPoint.commence(exchange, new AuthenticationCredentialsNotFoundException("Not Found"));
			}))
			.then(sc -> {
				return chain.filter(exchange);
			});
	}

}
