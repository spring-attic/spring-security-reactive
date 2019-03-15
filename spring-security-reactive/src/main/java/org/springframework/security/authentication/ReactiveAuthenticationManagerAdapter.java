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
package org.springframework.security.authentication;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;

import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

/**
 * 
 * @author Rob Winch
 * @since 5.0
 */
public class ReactiveAuthenticationManagerAdapter implements ReactiveAuthenticationManager {
	private final AuthenticationManager authenticationManager;

	public ReactiveAuthenticationManagerAdapter(AuthenticationManager authenticationManager) {
		this.authenticationManager = authenticationManager;
	}

	@Override
	public Mono<Authentication> authenticate(Authentication token) {
		return Mono.<Authentication>just(token)
			.publishOn(Schedulers.elastic())
			.flatMap( t -> {
				try {
					return Mono.just(authenticationManager.authenticate(t));
				} catch(Throwable error) {
					return Mono.error(error);
				}
			});
	}
}