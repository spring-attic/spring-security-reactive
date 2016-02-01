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
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.DispatcherHandler;
import org.springframework.web.server.adapter.WebHttpHandlerBuilder;


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

	private String cookieHeader(ResponseEntity<?> entity) {
		return entity.getHeaders().getFirst("Set-Cookie");
	}

	private String authorization() {
		String credentials =  Base64.getEncoder().encodeToString("rob:rob".getBytes(Charset.defaultCharset()));
		return "Basic " + credentials;
	}
}
