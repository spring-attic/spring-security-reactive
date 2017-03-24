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
package org.springframework.security.config.web.server;

import org.springframework.security.access.SecurityConfig;
import org.springframework.security.web.server.AuthorizationWebFilter;
import org.springframework.security.web.server.access.expression.ExpressionReactiveAccessDecisionManager;
import org.springframework.security.web.server.access.expression.ServerWebExchangeMetadataSource;
import org.springframework.security.web.server.access.expression.ServerWebExchangeMetadataSource.Builder;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatcher;
import org.springframework.web.server.WebFilter;

import java.util.List;

public class AuthorizeRequestBuilder extends AbstractServerWebExchangeMatcherRegistry<AuthorizeRequestBuilder.Access> {
	private Builder metadataSource = ServerWebExchangeMetadataSource.builder();
	private ExpressionReactiveAccessDecisionManager manager = new ExpressionReactiveAccessDecisionManager();
	private ServerWebExchangeMatcher matcher;

	@Override
	protected Access registerMatcher(ServerWebExchangeMatcher matcher) {
		this.matcher = matcher;
		return new Access();
	}

	public WebFilter build() {
		ServerWebExchangeMetadataSource metadataSource = this.metadataSource
				.build();
		return new AuthorizationWebFilter(manager, metadataSource );
	}

	public final class Access {
		
		public void permitAll() {
			access("permitAll");
		}

		public void hasRole(String role) {
			access("hasRole('"+ role +"')");
		}

		public void authenticated() {
			access("authenticated");
		}

		public void access(String configAttr) {
			SecurityConfig config = new SecurityConfig(configAttr);
			metadataSource.add(matcher, config);
			matcher = null;
			config = null;
		}
	}
}