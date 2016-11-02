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
package org.springframework.security.web.server.access.expression;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatcher;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Flux;

/**
 *
 * @author Rob Winch
 * @since 5.0
 */
public class ServerWebExchangeMetadataSource {
	private final LinkedHashMap<ServerWebExchangeMatcher,SecurityConfig> mappings;

	private ServerWebExchangeMetadataSource(LinkedHashMap<ServerWebExchangeMatcher, SecurityConfig> mappings) {
		this.mappings = mappings;
	}

	public Flux<ConfigAttribute> getConfigAttributes(ServerWebExchange exchange) {
		for(Map.Entry<ServerWebExchangeMatcher,SecurityConfig> entry : mappings.entrySet()) {
			if(entry.getKey().matches(exchange).isMatch()) {
				return Flux.just(entry.getValue());
			}
		}
		return Flux.empty();
	}

	public static Builder builder() {
		return new Builder();
	}

	public static class Builder {
		private final LinkedHashMap<ServerWebExchangeMatcher,SecurityConfig> mappings = new LinkedHashMap<>();

		private Builder() {}

		public Builder add(ServerWebExchangeMatcher matcher, SecurityConfig config) {
			this.mappings.put(matcher, config);
			return this;
		}

		public ServerWebExchangeMetadataSource build() {
			return new ServerWebExchangeMetadataSource(mappings);
		}
	}
}
