package sample;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.web.client.reactive.ClientWebRequestPostProcessors.httpBasic;
import static org.springframework.web.client.reactive.ClientWebRequestBuilders.get;
import static org.springframework.web.client.reactive.ResponseExtractors.response;

import java.nio.charset.Charset;
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
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.reactive.ClientWebRequestPostProcessor;
import org.springframework.web.client.reactive.DefaultClientWebRequestBuilder;
import org.springframework.web.client.reactive.ResponseExtractor;
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
		this.webClient = new WebClient(new ReactorClientHttpConnector());
	}

	@Test
	public void basicRequired() throws Exception {
		Mono<ResponseEntity<Map<String,String>>> response = this.webClient
				.perform(peopleRequest())
				.extract(mapStringString());

		assertThat(response.block().getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
	}

	@Test
	public void basicWorks() throws Exception {
		Mono<ResponseEntity<Map<String,String>>> response = this.webClient
				.perform(peopleRequest().apply(robsCredentials()))
				.extract(mapStringString());

		assertThat(response.block().getStatusCode()).isEqualTo(HttpStatus.OK);
	}

	@Test
	public void authorizationAdmin401() throws Exception {
		Mono<ResponseEntity<Map<String,String>>> response = this.webClient
				.perform(adminRequest().apply(robsCredentials()))
				.extract(mapStringString());

		assertThat(response.block().getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
	}

	@Test
	public void authorizationAdmin200() throws Exception {
		Mono<ResponseEntity<Map<String,String>>> response = this.webClient
				.perform(adminRequest().apply(adminCredentials()))
				.extract(mapStringString());

		assertThat(response.block().getStatusCode()).isEqualTo(HttpStatus.OK);
	}

	@Test
	public void basicMissingUser401() throws Exception {
		Mono<ResponseEntity<Map<String,String>>> response = this.webClient
				.perform(peopleRequest().apply(httpBasic("missing-user","rob")))
				.extract(mapStringString());

		assertThat(response.block().getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
	}

	@Test
	public void basicInvalidPassword401() throws Exception {
		Mono<ResponseEntity<Map<String,String>>> response = this.webClient
				.perform(peopleRequest().apply(httpBasic("rob","invalid")))
				.extract(mapStringString());

		assertThat(response.block().getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
	}

	@Test
	public void basicInvalidParts401() throws Exception {
		Mono<ResponseEntity<Map<String,String>>> response = this.webClient
				.perform(peopleRequest().header("Authorization", "Basic " + base64Encode("no colon")))
				.extract(mapStringString());

		assertThat(response.block().getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
	}

	@Test
	public void sessionWorks() throws Exception {
		Mono<ResponseEntity<Map<String,String>>> response = this.webClient
				.perform(peopleRequest().apply(robsCredentials()))
				.extract(mapStringString());

		String session = response.block().getHeaders().getFirst("Set-Cookie");

		response = this.webClient
				.perform(peopleRequest().header("Cookie", session))
				.extract(mapStringString());

		assertThat(response.block().getStatusCode()).isEqualTo(HttpStatus.OK);
	}

	@Test
	public void me() throws Exception {
		Mono<ResponseEntity<Map<String,String>>> response = this.webClient
				.perform(meRequest().apply(robsCredentials()))
				.extract(mapStringString());

		assertThat(response.block().getBody()).hasSize(1).containsEntry("username", "rob");
	}

	private ClientWebRequestPostProcessor robsCredentials() {
		return httpBasic("rob","rob");
	}

	private ClientWebRequestPostProcessor adminCredentials() {
		return httpBasic("admin","admin");
	}

	private DefaultClientWebRequestBuilder adminRequest() {
		return get("http://localhost:{port}/admin", port).accept(MediaType.APPLICATION_JSON);
	}

	private DefaultClientWebRequestBuilder peopleRequest() {
		return get("http://localhost:{port}/people", port).accept(MediaType.APPLICATION_JSON);
	}

	private DefaultClientWebRequestBuilder meRequest() {
		return get("http://localhost:{port}/me",port).accept(MediaType.APPLICATION_JSON);
	}

	private String base64Encode(String value) {
		return Base64.getEncoder().encodeToString(value.getBytes(Charset.defaultCharset()));
	}

	private ResponseExtractor<Mono<ResponseEntity<Map<String, String>>>> mapStringString() {
		return response(ResolvableType.forClassWithGenerics(Map.class, String.class, String.class));
	}
}
