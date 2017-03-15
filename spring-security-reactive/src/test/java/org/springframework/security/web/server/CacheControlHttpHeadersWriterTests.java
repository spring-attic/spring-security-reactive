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
package org.springframework.security.web.server;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;

import org.junit.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.security.test.web.reactive.server.ServerWebExchangeBuilders;
import org.springframework.web.server.ServerWebExchange;

/**
 *
 * @author Rob Winch
 * @since 5.0
 *
 */
public class CacheControlHttpHeadersWriterTests {
	CacheControlHttpHeadersWriter writer = new CacheControlHttpHeadersWriter();

	ServerWebExchange exchange = ServerWebExchangeBuilders.toExchange(MockServerHttpRequest.get("/"));

	HttpHeaders headers = exchange.getResponse().getHeaders();

	@Test
	public void writeHeadersWhenCacheHeadersThenWritesAllCacheControl() {
		writer.writeHttpHeaders(exchange);

		assertThat(headers).containsEntry(HttpHeaders.CACHE_CONTROL, Arrays.asList("no-cache, no-store, max-age=0, must-revalidate"));
		assertThat(headers).containsEntry(HttpHeaders.EXPIRES, Arrays.asList("0"));
		assertThat(headers).containsEntry(HttpHeaders.PRAGMA, Arrays.asList("no-cache"));
	}

	@Test
	public void writeHeadersWhenCacheControlThenNoCacheControlHeaders() {
		String cacheControl = "max-age=1234";

		headers.set(HttpHeaders.CACHE_CONTROL, cacheControl);

		writer.writeHttpHeaders(exchange);

		assertThat(headers).containsEntry(HttpHeaders.CACHE_CONTROL, Arrays.asList(cacheControl));
		assertThat(headers).doesNotContainKey(HttpHeaders.EXPIRES);
		assertThat(headers).doesNotContainKey(HttpHeaders.PRAGMA);
	}

	@Test
	public void writeHeadersWhenPragmaThenNoCacheControlHeaders() {
		String pragma = "1";
		headers.set(HttpHeaders.PRAGMA, pragma);

		writer.writeHttpHeaders(exchange);

		assertThat(headers).containsEntry(HttpHeaders.PRAGMA, Arrays.asList(pragma));
		assertThat(headers).doesNotContainKey(HttpHeaders.CACHE_CONTROL);
		assertThat(headers).doesNotContainKey(HttpHeaders.EXPIRES);
	}

	@Test
	public void writeHeadersWhenExpiresThenNoCacheControlHeaders() {
		String expires = "1";
		headers.set(HttpHeaders.EXPIRES, expires);

		writer.writeHttpHeaders(exchange);

		assertThat(headers).containsEntry(HttpHeaders.EXPIRES, Arrays.asList(expires));
		assertThat(headers).doesNotContainKey(HttpHeaders.CACHE_CONTROL);
		assertThat(headers).doesNotContainKey(HttpHeaders.PRAGMA);
	}

}
