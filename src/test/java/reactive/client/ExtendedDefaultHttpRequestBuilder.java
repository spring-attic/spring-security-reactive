package reactive.client;

import java.net.URI;

import org.reactivestreams.Publisher;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.reactive.DefaultHttpRequestBuilder;

public class ExtendedDefaultHttpRequestBuilder extends DefaultHttpRequestBuilder {


	public ExtendedDefaultHttpRequestBuilder() {
		super();
	}

	public ExtendedDefaultHttpRequestBuilder(HttpMethod httpMethod, String urlTemplate, Object... urlVariables)
			throws RestClientException {
		super(httpMethod, urlTemplate, urlVariables);
	}

	public ExtendedDefaultHttpRequestBuilder(HttpMethod httpMethod, URI url) {
		super(httpMethod, url);
	}

	@Override
	public ExtendedDefaultHttpRequestBuilder param(String name, String... values) {
		return (ExtendedDefaultHttpRequestBuilder) super.param(name, values);
	}

	@Override
	public ExtendedDefaultHttpRequestBuilder header(String name, String... values) {
		return (ExtendedDefaultHttpRequestBuilder) super.header(name, values);
	}

	@Override
	public ExtendedDefaultHttpRequestBuilder headers(HttpHeaders httpHeaders) {
		return (ExtendedDefaultHttpRequestBuilder) super.headers(httpHeaders);
	}

	@Override
	public ExtendedDefaultHttpRequestBuilder contentType(MediaType contentType) {
		return (ExtendedDefaultHttpRequestBuilder) super.contentType(contentType);
	}

	@Override
	public ExtendedDefaultHttpRequestBuilder contentType(String contentType) {
		return (ExtendedDefaultHttpRequestBuilder) super.contentType(contentType);
	}

	@Override
	public ExtendedDefaultHttpRequestBuilder accept(MediaType... mediaTypes) {
		return (ExtendedDefaultHttpRequestBuilder) super.accept(mediaTypes);
	}

	@Override
	public ExtendedDefaultHttpRequestBuilder accept(String... mediaTypes) {
		return (ExtendedDefaultHttpRequestBuilder) super.accept(mediaTypes);
	}

	@Override
	public ExtendedDefaultHttpRequestBuilder content(Object content) {
		return (ExtendedDefaultHttpRequestBuilder) super.content(content);
	}

	@Override
	public ExtendedDefaultHttpRequestBuilder contentStream(Publisher content) {
		return (ExtendedDefaultHttpRequestBuilder) super.contentStream(content);
	}

	public ExtendedDefaultHttpRequestBuilder with(RequestPostProcessor with) {
		return with.postProcess(this);
	}

}
