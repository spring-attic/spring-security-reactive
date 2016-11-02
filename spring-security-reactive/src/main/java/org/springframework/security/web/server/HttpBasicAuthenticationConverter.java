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

import java.util.Base64;

import org.springframework.core.convert.converter.Converter;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

/**
 * 
 * @author Rob Winch
 * @since 5.0
 */
public class HttpBasicAuthenticationConverter implements Converter<ServerWebExchange,Mono<Authentication>> {

	@Override
	public Mono<Authentication> convert(ServerWebExchange exchange) {
		ServerHttpRequest request = exchange.getRequest();
		String authorization = request.getHeaders().getFirst("Authorization");
		if(authorization == null) {
			return Mono.empty();
		}

		String credentials = authorization.substring("Basic ".length(), authorization.length());
		byte[] decodedCredentials = Base64.getDecoder().decode(credentials);
		String decodedAuthz = new String(decodedCredentials);
		String[] userParts = decodedAuthz.split(":");

		if(userParts.length != 2) {
			return Mono.empty();
		}

		String username = userParts[0];
		String password = userParts[1];

		return Mono.just(new UsernamePasswordAuthenticationToken(username, password));
	}
}
