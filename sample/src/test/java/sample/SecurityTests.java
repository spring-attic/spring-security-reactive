/*
 * Copyright 2002-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *	  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package sample;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.web.reactive.function.client.ExchangeFilterFunctions.basicAuthentication;

import java.nio.charset.Charset;
import java.time.Duration;
import java.util.Base64;
import java.util.Collections;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ResolvableType;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.ExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SecurityTests {
	private static final ResolvableType MAP_OF_STRING_STRING = ResolvableType.forClassWithGenerics(Map.class, String.class, String.class);

	private WebTestClient rest;

	@LocalServerPort
	private int port;

	@Before
	public void setup() {
		this.rest = WebTestClient.bindToServer()
				.baseUrl("http://localhost:" + this.port)
				.build();
	}

	@Test
	public void basicRequired() throws Exception {
		ExchangeResult<String> result = this.rest
			.get()
			.uri("/users")
			.exchange()
			.decodeEntity(String.class);

		assertThat(result.getResponseStatus()).isEqualTo(HttpStatus.UNAUTHORIZED);
	}

	@Test
	public void basicWorks() throws Exception {
		ExchangeResult<String> result = this.rest
			.filter(robsCredentials())
			.get()
			.uri("/users")
			.exchange()
			.decodeEntity(String.class);

		assertThat(result.getResponseStatus()).isEqualTo(HttpStatus.OK);
	}

	@Test
	public void authorizationAdmin401() throws Exception {
		ExchangeResult<String> result = this.rest
			.filter(robsCredentials())
			.get()
			.uri("/admin")
			.exchange()
			.decodeEntity(String.class);

		assertThat(result.getResponseStatus()).isEqualTo(HttpStatus.UNAUTHORIZED);
	}

	@Test
	public void authorizationAdmin200() throws Exception {
		ExchangeResult<String> result = this.rest
			.filter(adminCredentials())
			.get()
			.uri("/admin")
			.exchange()
			.decodeEntity(String.class);

		assertThat(result.getResponseStatus()).isEqualTo(HttpStatus.OK);
	}

	@Test
	public void basicMissingUser401() throws Exception {
		ExchangeResult<String> result = this.rest
			.filter(basicAuthentication("missing-user", "password"))
			.get()
			.uri("/admin")
			.exchange()
			.decodeEntity(String.class);

		assertThat(result.getResponseStatus()).isEqualTo(HttpStatus.UNAUTHORIZED);
	}

	@Test
	public void basicInvalidPassword401() throws Exception {
		ExchangeResult<String> result = this.rest
			.filter(basicAuthentication("rob", "invalid"))
			.get()
			.uri("/admin")
			.exchange()
			.decodeEntity(String.class);

		assertThat(result.getResponseStatus()).isEqualTo(HttpStatus.UNAUTHORIZED);
	}

	@Test
	public void basicInvalidParts401() throws Exception {
		ExchangeResult<String> result = this.rest
			.get()
			.uri("/admin")
			.header("Authorization", "Basic " + base64Encode("no colon"))
			.exchange()
			.decodeEntity(String.class);

		assertThat(result.getResponseStatus()).isEqualTo(HttpStatus.UNAUTHORIZED);
	}

	@Test
	public void sessionWorks() throws Exception {
		ExchangeResult<String> result = this.rest
				.filter(robsCredentials())
				.get()
				.uri("/users")
				.exchange()
				.decodeEntity(String.class);

		String session = result.getResponseHeaders().getFirst("Set-Cookie");

		result = this.rest
			.get()
			.uri("/users")
			.header("Cookie", session)
			.exchange()
			.decodeEntity(String.class);

		assertThat(result.getResponseStatus()).isEqualTo(HttpStatus.OK);
	}

	@Test
	public void me() throws Exception {
		Map<String,String> expected = Collections.singletonMap("username", "rob");

		this.rest
			.filter(robsCredentials())
			.get()
			.uri("/me")
			.exchange()
			.decodeEntity(MAP_OF_STRING_STRING)
			.assertThat()
			.status().isOk()
			.bodyEquals(expected);
	}

	@Test
	public void principal() throws Exception {
		Map<String,String> expected = Collections.singletonMap("username", "rob");

		this.rest
				.filter(robsCredentials())
				.get()
				.uri("/principal")
				.exchange()
				.decodeEntity(MAP_OF_STRING_STRING)
				.assertThat()
				.status().isOk()
				.bodyEquals(expected);
	}

	private ExchangeFilterFunction robsCredentials() {
		return basicAuthentication("rob","rob");
	}

	private ExchangeFilterFunction adminCredentials() {
		return basicAuthentication("admin","admin");
	}

	private String base64Encode(String value) {
		return Base64.getEncoder().encodeToString(value.getBytes(Charset.defaultCharset()));
	}
}
