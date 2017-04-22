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

import org.springframework.core.annotation.Order;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.server.context.WebSessionSecurityContextRepository;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import reactor.core.publisher.Mono;

/**
 *
 * @author Rob Winch
 * @since 5.0
 */
@Order(0)
public class AuthenticationWebFilter implements WebFilter {

	private WebSessionSecurityContextRepository securityContextRepository;

	private Converter<ServerWebExchange,Mono<Authentication>> authenticationConverter;

	private ReactiveAuthenticationManager authenticationManager;

	private AuthenticationEntryPoint entryPoint;

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
		return authenticationConverter.convert(exchange)
                .then(token -> authenticationManager.authenticate(token)
                        .then(authentication -> {
                            SecurityContext context = new SecurityContextImpl();
                            context.setAuthentication(authentication);
                            return securityContextRepository.save(exchange, context);
                        })
                        .otherwise(AuthenticationException.class, t -> entryPoint.commence(exchange, t)))
			    .then(Mono.defer(() -> chain.filter(exchange)));
	}

	public void setSecurityContextRepository(WebSessionSecurityContextRepository securityContextRepository) {
		this.securityContextRepository = securityContextRepository;
	}

	public void setAuthenticationConverter(Converter<ServerWebExchange,Mono<Authentication>> authenticationConverter) {
		this.authenticationConverter = authenticationConverter;
	}

	public void setAuthenticationManager(ReactiveAuthenticationManager authenticationManager) {
		this.authenticationManager = authenticationManager;
	}

	public void setEntryPoint(AuthenticationEntryPoint entryPoint) {
		this.entryPoint = entryPoint;
	}
}