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
package org.springframework.security.web.server.util.matcher;

import java.util.Collections;
import java.util.Map;

import org.springframework.web.server.ServerWebExchange;

/**
 *
 * @author Rob Winch
 * @since 5.0
 */
public interface ServerWebExchangeMatcher {

	MatchResult matches(ServerWebExchange exchange);

	class MatchResult {
		private final boolean match;
		private final Map<String,Object> variables;

		private MatchResult(boolean match, Map<String, Object> variables) {
			this.match = match;
			this.variables = variables;
		}

		public boolean isMatch() {
			return match;
		}

		public Map<String,Object> getVariables() {
			return variables;
		}

		public static MatchResult match() {
			return match(Collections.emptyMap());
		}

		public static MatchResult match(Map<String,Object> variables) {
			return new MatchResult(true, variables);
		}

		public static MatchResult notMatch() {
			return new MatchResult(false, Collections.emptyMap());
		}
	}
}
