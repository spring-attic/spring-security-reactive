/*
 *
 *  * Copyright 2017 the original author or authors.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *      https://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package org.springframework.security.web.server.util.matcher;

import org.junit.Test;
import org.springframework.http.HttpMethod;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers.antMatchers;
import static org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers.anyExchange;

/**
 * @author Rob Winch
 * @since 5.0
 */
public class ServerWebExchangeMatchersTests {
	ServerWebExchange exchange = MockServerHttpRequest.get("/").toExchange();

	@Test
	public void antMatchersWhenSingleAndSamePatternThenMatches() throws Exception {
		assertThat(antMatchers("/").matches(exchange).isMatch()).isTrue();
	}

	@Test
	public void antMatchersWhenSingleAndSamePatternAndMethodThenMatches() throws Exception {
		assertThat(antMatchers(HttpMethod.GET, "/").matches(exchange).isMatch()).isTrue();
	}

	@Test
	public void antMatchersWhenSingleAndSamePatternAndDiffMethodThenDoesNotMatch() throws Exception {
		assertThat(antMatchers(HttpMethod.POST, "/").matches(exchange).isMatch()).isFalse();
	}

	@Test
	public void antMatchersWhenSingleAndDifferentPatternThenDoesNotMatch() throws Exception {
		assertThat(antMatchers("/foobar").matches(exchange).isMatch()).isFalse();
	}

	@Test
	public void antMatchersWhenMultiThenMatches() throws Exception {
		assertThat(antMatchers("/foobar", "/").matches(exchange).isMatch()).isTrue();
	}

	@Test
	public void anyExchangeWhenMockThenMatches() {
		ServerWebExchange mockExchange = mock(ServerWebExchange.class);

		assertThat(anyExchange().matches(mockExchange).isMatch()).isTrue();

		verifyZeroInteractions(mockExchange);
	}

	/**
	 * If a LinkedMap is used and anyRequest equals anyRequest then the following is added:
	 * anyRequest() -> authenticated()
	 * antMatchers("/admin/**") -> hasRole("ADMIN")
	 * anyRequest() -> permitAll
	 *
	 * will result in the first entry being overridden
	 */
	@Test
	public void anyExchangeWhenTwoCreatedThenDifferentToPreventIssuesInMap() {
		assertThat(anyExchange()).isNotEqualTo(anyExchange());
	}
}