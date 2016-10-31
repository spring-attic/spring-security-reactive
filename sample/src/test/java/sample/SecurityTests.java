package sample;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.codec.BodyExtractors.toMono;
import static org.springframework.web.client.reactive.ClientRequest.GET;
import static org.springframework.web.client.reactive.ExchangeFilterFunctions.basicAuthentication;

import java.nio.charset.Charset;
import java.util.Base64;
import java.util.Map;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ResolvableType;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.reactive.ClientRequest;
import org.springframework.web.client.reactive.ClientRequest.HeadersBuilder;
import org.springframework.web.client.reactive.ClientResponse;
import org.springframework.web.client.reactive.ExchangeFilterFunction;
import org.springframework.web.client.reactive.ExchangeFilterFunctions;
import org.springframework.web.client.reactive.ExchangeFunction;
import org.springframework.web.client.reactive.WebClient;

import reactor.core.publisher.Mono;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SecurityTests {
	private WebClient webClient;

	@LocalServerPort
	private int port;

	@Before
	public void setup() {
		this.webClient = WebClient
				.builder(new ReactorClientHttpConnector())
				.build();
	}

	@Test
	public void basicRequired() throws Exception {
		Mono<HttpStatus> response = this.webClient
				.exchange(usersRequest().build())
				.then(this::httpStatus);

		assertThat(response.block()).isEqualTo(HttpStatus.UNAUTHORIZED);
	}

	@Test
	public void basicWorks() throws Exception {
		ClientRequest<Void> request = usersRequest().build();

		ExchangeFunction client = robsCredentials().apply(this.webClient::exchange);
		Mono<HttpStatus> response = client.exchange(request)
				.then(this::httpStatus);

		assertThat(response.block()).isEqualTo(HttpStatus.OK);
	}

	@Test
	public void authorizationAdmin401() throws Exception {
		ExchangeFunction client = robsCredentials().apply(this.webClient::exchange);
		Mono<HttpStatus> response = client
				.exchange(adminRequest().build())
				.then(this::httpStatus);

		assertThat(response.block()).isEqualTo(HttpStatus.UNAUTHORIZED);
	}

	@Test
	public void authorizationAdmin200() throws Exception {
		ExchangeFunction client = adminCredentials().apply(this.webClient::exchange);
		Mono<HttpStatus> response = client
				.exchange(adminRequest().build())
				.then(this::httpStatus);

		assertThat(response.block()).isEqualTo(HttpStatus.OK);
	}

	@Test
	public void basicMissingUser401() throws Exception {
		ExchangeFunction client = basicAuthentication("missing-user", "password")
				.apply(this.webClient::exchange);
		Mono<HttpStatus> response = client
				.exchange(adminRequest().build())
				.then(this::httpStatus);

		assertThat(response.block()).isEqualTo(HttpStatus.UNAUTHORIZED);
	}

	@Test
	public void basicInvalidPassword401() throws Exception {
		ExchangeFunction client = basicAuthentication("rob","invalid")
				.apply(this.webClient::exchange);
		Mono<HttpStatus> response = client
				.exchange(adminRequest().build())
				.then(this::httpStatus);

		assertThat(response.block()).isEqualTo(HttpStatus.UNAUTHORIZED);
	}

	@Test
	public void basicInvalidParts401() throws Exception {
		Mono<HttpStatus> response = this.webClient
				.exchange(usersRequest().header("Authorization", "Basic " + base64Encode("no colon")).build())
				.then(this::httpStatus);

		assertThat(response.block()).isEqualTo(HttpStatus.UNAUTHORIZED);
	}

	@Test
	public void sessionWorks() throws Exception {
		ExchangeFunction robsClient = robsCredentials()
				.apply(this.webClient::exchange);
		Mono<ClientResponse> response = robsClient
				.exchange(usersRequest().build());

		String session = response.block().headers().asHttpHeaders().getFirst("Set-Cookie");

		response = this.webClient
				.exchange(usersRequest().header("Cookie", session).build());

		assertThat(response.block().statusCode()).isEqualTo(HttpStatus.OK);
	}

	@Test
	public void me() throws Exception {
		ExchangeFunction robsClient = robsCredentials()
				.apply(this.webClient::exchange);
		Mono<Map<String,String>> response = robsClient
				.exchange(meRequest().build())
				.then( result -> result.body(toMono(ResolvableType.forClassWithGenerics(Map.class, String.class, String.class))));

		assertThat(response.block()).hasSize(1).containsEntry("username", "rob");
	}

	private ExchangeFilterFunction robsCredentials() {
		return basicAuthentication("rob","rob");
	}

	private ExchangeFilterFunction adminCredentials() {
		return basicAuthentication("admin","admin");
	}

	private HeadersBuilder<?> adminRequest() {
		return GET("http://localhost:{port}/admin", port).accept(MediaType.APPLICATION_JSON);
	}

	private HeadersBuilder<?> usersRequest() {
		return GET("http://localhost:{port}/users", port).accept(MediaType.APPLICATION_JSON);
	}

	private HeadersBuilder<?>  meRequest() {
		return GET("http://localhost:{port}/me",port).accept(MediaType.APPLICATION_JSON);
	}

	private String base64Encode(String value) {
		return Base64.getEncoder().encodeToString(value.getBytes(Charset.defaultCharset()));
	}

	private Mono<HttpStatus> httpStatus(ClientResponse response) {
		return Mono.just(response.statusCode());
	}
}
