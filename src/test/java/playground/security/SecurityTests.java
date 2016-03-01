package playground.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.Base64;

import org.junit.After;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.HttpHandler;
import org.springframework.web.client.HttpClientErrorException;

import playground.Application;

public class SecurityTests extends AbstractHttpHandlerIntegrationTests {

	private AnnotationConfigApplicationContext wac;

	@After
	public void closeWac() {
		try {
			wac.close();
		}catch(Exception ignore) {}
	}

	@Override
	protected HttpHandler createHttpHandler() throws IOException {
		return Application.createHttpHandler();
	}

	@Test
	public void basicRequired() throws Exception {
		URI url = new URI("http://localhost:" + port + "/people");
		RequestEntity<Void> request = RequestEntity.get(url).build();
		assertThatThrownBy( () -> rest.exchange(request, String.class))
			.isInstanceOf(HttpClientErrorException.class)
			.hasMessage("401 Unauthorized");
	}

	@Test
	public void basicWorks() throws Exception {
		URI url = new URI("http://localhost:" + port + "/people");
		RequestEntity<Void> request = RequestEntity
				.get(url)
				.header("Authorization", authorization())
				.build();
		ResponseEntity<String> result = rest.exchange(request, String.class);
		assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
	}

	@Test
	public void basicMissingUser401() throws Exception {
		URI url = new URI("http://localhost:" + port + "/people");
		RequestEntity<Void> request = RequestEntity
				.get(url)
				.header("Authorization", authorization("missing-user","password"))
				.build();
		assertThatThrownBy( () -> rest.exchange(request, String.class))
			.isInstanceOf(HttpClientErrorException.class)
			.hasMessage("401 Unauthorized");
	}

	@Test
	public void basicInvalidPassword401() throws Exception {
		URI url = new URI("http://localhost:" + port + "/people");
		RequestEntity<Void> request = RequestEntity
				.get(url)
				.header("Authorization", authorization("rob","invalid"))
				.build();
		assertThatThrownBy( () -> rest.exchange(request, String.class))
			.isInstanceOf(HttpClientErrorException.class)
			.hasMessage("401 Unauthorized");
	}

	@Test
	public void basicInvalidParts401() throws Exception {
		URI url = new URI("http://localhost:" + port + "/people");
		RequestEntity<Void> request = RequestEntity
				.get(url)
				.header("Authorization", authorization("no collon"))
				.build();
		assertThatThrownBy( () -> rest.exchange(request, String.class))
			.isInstanceOf(HttpClientErrorException.class)
			.hasMessage("401 Unauthorized");
	}

	@Test
	public void sessionWorks() throws Exception {
		URI url = new URI("http://localhost:" + port + "/people");
		RequestEntity<Void> request = RequestEntity
				.get(url)
				.header("Authorization", authorization())
				.build();

		ResponseEntity<String> result = rest.exchange(request, String.class);

		request = RequestEntity
				.get(url)
				.header("Cookie", cookieHeader(result))
				.build();
		result = rest.exchange(request, String.class);
		assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
	}

	@Test
	public void me() throws Exception {
		URI url = new URI("http://localhost:" + port + "/me");
		RequestEntity<Void> request = RequestEntity
				.get(url)
				.header("Authorization", authorization())
				.build();
		ResponseEntity<String> result = rest.exchange(request, String.class);
		assertThat(result.getBody()).isEqualTo("rob");
	}

	private String cookieHeader(ResponseEntity<?> entity) {
		return entity.getHeaders().getFirst("Set-Cookie");
	}

	private String authorization(String username, String password) {
		String credentials = username+":"+password;
		return authorization(credentials);
	}

	private String authorization(String credentials) {
		String encodedCredentials =  Base64.getEncoder().encodeToString(credentials.getBytes(Charset.defaultCharset()));
		return "Basic " + encodedCredentials;
	}

	private String authorization() {
		return authorization("rob","rob");
	}
}
