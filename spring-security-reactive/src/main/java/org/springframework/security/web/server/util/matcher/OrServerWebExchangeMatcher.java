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
package org.springframework.security.web.server.util.matcher;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

import org.springframework.util.Assert;
import org.springframework.web.server.ServerWebExchange;

/**
 * @author Rob Winch
 * @since 5.0
 */
public class OrServerWebExchangeMatcher implements ServerWebExchangeMatcher {
	private final List<ServerWebExchangeMatcher> matchers;

	public OrServerWebExchangeMatcher(List<ServerWebExchangeMatcher> matchers) {
		Assert.notEmpty(matchers, "matchers cannot be empty");
		this.matchers = matchers;
	}


	public OrServerWebExchangeMatcher(ServerWebExchangeMatcher... matchers) {
		this(Arrays.asList(matchers));
	}

	/* (non-Javadoc)
	 * @see org.springframework.security.web.server.util.matcher.ServerWebExchangeMatcher#matches(org.springframework.web.server.ServerWebExchange)
	 */
	@Override
	public MatchResult matches(ServerWebExchange exchange) {
		return matchers.stream()
			.map(m -> m.matches(exchange))
			.filter(m -> m.isMatch())
			.findFirst()
			.orElse(MatchResult.notMatch());
	}

	@Override
	public String toString() {
		return "OrServerWebExchangeMatcher{" +
				"matchers=" + matchers +
				'}';
	}
}
