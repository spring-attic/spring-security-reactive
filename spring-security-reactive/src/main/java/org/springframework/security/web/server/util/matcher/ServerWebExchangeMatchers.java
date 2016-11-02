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
package org.springframework.security.web.server.util.matcher;

import java.util.Collections;

import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatcher.MatchResult;
import org.springframework.web.server.ServerWebExchange;

public abstract class ServerWebExchangeMatchers {
	private static final MatchResult ANY_MATCH = new MatchResult(true, Collections.emptyMap());

	public static ServerWebExchangeMatcher antMatchers(String pattern) {
		return new PathMatcherServerWebExchangeMatcher(pattern);
	}

	public static ServerWebExchangeMatcher anyExchange() {
		return new ServerWebExchangeMatcher() {
			@Override
			public MatchResult matches(ServerWebExchange exchange) {
				return ANY_MATCH;
			}
		};
	}

	private ServerWebExchangeMatchers() {}
}
