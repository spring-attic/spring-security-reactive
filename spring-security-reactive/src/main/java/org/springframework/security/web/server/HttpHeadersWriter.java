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
package org.springframework.security.web.server;

import java.util.function.Supplier;

import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

/**
 * Interface for writing headers just before the response is committed.
 *
 * @author Rob Winch
 * @since 5.0
 */
public interface HttpHeadersWriter {

	/**
	 * Write the headers to the response.
	 *
	 * @param exchange
	 * @return A Mono which is returned to the {@link Supplier} of the
	 *         {@link ServerHttpResponse#beforeCommit(Supplier)}.
	 */
	Mono<Void> writeHttpHeaders(ServerWebExchange exchange);
}
