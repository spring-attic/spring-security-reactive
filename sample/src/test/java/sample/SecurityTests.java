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
package sample;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.web.reactive.function.BodyExtractors.toMono;
import static org.springframework.web.reactive.function.client.ExchangeFilterFunctions.basicAuthentication;

import java.nio.charset.Charset;
import java.time.Duration;
import java.util.Base64;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ResolvableType;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientOperations;
import org.springframework.web.util.DefaultUriBuilderFactory;

import reactor.core.publisher.Mono;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SecurityTests {
	private static final Duration ONE_SECOND = Duration.ofSeconds(1);

	private static final ResolvableType MAP_OF_STRING_STRING = ResolvableType.forClassWithGenerics(Map.class, String.class, String.class);

	private WebClientOperations rest;

	@LocalServerPort
	private int port;

	@Before
	public void setup() {
		WebClient webClient = WebClient
				.builder(new ReactorClientHttpConnector())
				.build();

		this.rest = WebClientOperations
				.builder(webClient)
				.uriBuilderFactory(new DefaultUriBuilderFactory("http://localhost:"+ port))
				.build();
	}

	@Test
	public void basicRequired() throws Exception {
		Mono<HttpStatus> response = this.rest
				.get()
				.uri("/users")
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.then(this::httpStatus);

		assertThat(response.block(ONE_SECOND)).isEqualTo(HttpStatus.UNAUTHORIZED);
	}

	@Test
	public void basicWorks() throws Exception {
		Mono<HttpStatus> response =  this.rest
				.filter(robsCredentials())
				.get()
				.uri("/users")
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.then(this::httpStatus);

		assertThat(response.block(ONE_SECOND)).isEqualTo(HttpStatus.OK);
	}

	@Test
	public void authorizationAdmin401() throws Exception {
		Mono<HttpStatus> response = this.rest
				.filter(robsCredentials())
				.get()
				.uri("/admin")
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.then(this::httpStatus);

		assertThat(response.block(ONE_SECOND)).isEqualTo(HttpStatus.UNAUTHORIZED);
	}

	@Test
	public void authorizationAdmin200() throws Exception {
		Mono<HttpStatus> response = this.rest
				.filter(adminCredentials())
				.get()
				.uri("/admin")
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.then(this::httpStatus);

		assertThat(response.block(ONE_SECOND)).isEqualTo(HttpStatus.OK);
	}

	@Test
	public void basicMissingUser401() throws Exception {
		Mono<HttpStatus> response = this.rest
				.filter(basicAuthentication("missing-user", "password"))
				.get()
				.uri("/admin")
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.then(this::httpStatus);

		assertThat(response.block(ONE_SECOND)).isEqualTo(HttpStatus.UNAUTHORIZED);
	}

	@Test
	public void basicInvalidPassword401() throws Exception {
		Mono<HttpStatus> response = this.rest
				.filter(basicAuthentication("rob", "invalid"))
				.get()
				.uri("/admin")
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.then(this::httpStatus);

		assertThat(response.block(ONE_SECOND)).isEqualTo(HttpStatus.UNAUTHORIZED);
	}

	@Test 
	public void basicInvalidParts401() throws Exception {
		Mono<HttpStatus> response = this.rest
				.get()
				.uri("/admin")
				.accept(MediaType.APPLICATION_JSON)
				.header("Authorization", "Basic " + base64Encode("no colon"))
				.exchange()
				.then(this::httpStatus);

		assertThat(response.block(ONE_SECOND)).isEqualTo(HttpStatus.UNAUTHORIZED);
	}

	@Test
	public void sessionWorks() throws Exception {
		Mono<ClientResponse> response = this.rest
				.filter(robsCredentials())
				.get()
				.uri("/users")
				.accept(MediaType.APPLICATION_JSON)
				.exchange();

		String session = response.block(ONE_SECOND).headers().asHttpHeaders().getFirst("Set-Cookie");

		response = this.rest
				.get()
				.uri("/users")
				.accept(MediaType.APPLICATION_JSON)
				.header("Cookie", session)
				.exchange();

		assertThat(response.block(ONE_SECOND).statusCode()).isEqualTo(HttpStatus.OK);
	}

	@Test
	public void me() throws Exception {
		Mono<Map<String,String>> response = this.rest
				.filter(robsCredentials())
				.get()
				.uri("/me")
				.exchange()
				.then( result -> result.body(toMono(MAP_OF_STRING_STRING)));

		assertThat(response.block(ONE_SECOND)).hasSize(1).containsEntry("username", "rob");
	}

	@Test
	public void principal() throws Exception {
		Mono<Map<String,String>> response = this.rest
				.filter(robsCredentials())
				.get()
				.uri("/principal")
				.exchange()
				.then( result -> result.body(toMono(MAP_OF_STRING_STRING)));

		assertThat(response.block(ONE_SECOND)).hasSize(1).containsEntry("username", "rob");
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

	private Mono<HttpStatus> httpStatus(ClientResponse response) {
		return Mono.just(response.statusCode());
	}
}
