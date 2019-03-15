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
package org.springframework.security.access;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.core.Authentication;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.util.function.*;

/**
 *
 * @author Rob Winch
 * @since 5.0
 */
public class ReactiveAccessDecisionManagerAdapter implements ReactiveAccessDecisionManager<Object> {
	private final AccessDecisionManager accessDecisionManager;

	public ReactiveAccessDecisionManagerAdapter(AccessDecisionManager accessDecisionManager) {
		super();
		this.accessDecisionManager = accessDecisionManager;
	}

	public Mono<Boolean> decide(Authentication authentication, Object object, Flux<ConfigAttribute> configAttributes) {
		return Mono
			.just(Tuples.of(authentication, object, configAttributes))
			.publishOn(Schedulers.elastic())
			.filter((Tuple3<Authentication, Object, Flux<ConfigAttribute>> t) -> {
				Authentication auth = t.getT1();
				return auth != null && auth.isAuthenticated();
			})
			.flatMap((Function<Tuple3<Authentication, Object, Flux<ConfigAttribute>>, Mono<Boolean>>) t -> {
				List<ConfigAttribute> attrs = new ArrayList<>();
				t.getT3().toIterable().forEach(attrs::add);

				try {
					accessDecisionManager.decide(t.getT1(), t.getT2(), attrs);
					return Mono.just(true);
				} catch(AccessDeniedException fail) {
					return Mono.just(false);
				}
			});
	}
}