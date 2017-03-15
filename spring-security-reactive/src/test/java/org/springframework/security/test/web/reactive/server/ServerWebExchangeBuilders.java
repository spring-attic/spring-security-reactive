/*
 * Copyright 2017 the original author or authors.
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
package org.springframework.security.test.web.reactive.server;

import org.springframework.mock.http.server.reactive.MockServerHttpResponse;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest.BaseBuilder;
import org.springframework.web.server.adapter.DefaultServerWebExchange;

/**
 *
 * @author Rob Winch
 * @since 5.0
 */
public class ServerWebExchangeBuilders {

	public static DefaultServerWebExchange toExchange(BaseBuilder<?> baseBuilder) {
		return new DefaultServerWebExchange(baseBuilder.build(), new MockServerHttpResponse());
	}
}
