package org.springframework.security.web.server.util.matcher;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpMethod;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.http.server.reactive.MockServerHttpResponse;
import org.springframework.util.PathMatcher;
import org.springframework.web.server.adapter.DefaultServerWebExchange;
import org.springframework.web.server.session.DefaultWebSessionManager;

@RunWith(MockitoJUnitRunner.class)
public class PathMatcherServerWebExchangeMatcherTests {
	@Mock
	PathMatcher pathMatcher;
	DefaultServerWebExchange exchange;
	PathMatcherServerWebExchangeMatcher matcher;
	String pattern;
	String path;

	@Before
	public void setup() {
		MockServerHttpRequest request = new MockServerHttpRequest();
		request.setUri("/path");
		request.setHttpMethod(HttpMethod.POST);
		MockServerHttpResponse response = new MockServerHttpResponse();
		DefaultWebSessionManager sessionManager = new DefaultWebSessionManager();
		exchange = new DefaultServerWebExchange(request, response, sessionManager);
		pattern = "/pattern";
		path = "/path";

		matcher = new PathMatcherServerWebExchangeMatcher(pattern);
		matcher.setPathMatcher(pathMatcher);
	}

	@Test(expected = IllegalArgumentException.class)
	public void constructorPatternWhenPatternNullThenThrowsException() {
		new PathMatcherServerWebExchangeMatcher(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void constructorPatternAndMethodWhenPatternNullThenThrowsException() {
		new PathMatcherServerWebExchangeMatcher(null, HttpMethod.GET);
	}

	@Test
	public void matchesWhenPathMatcherTrueThenReturnTrue() {
		when(pathMatcher.match(pattern, path)).thenReturn(true);

		assertThat(matcher.matches(exchange)).isTrue();
	}

	@Test
	public void matchesWhenPathMatcherFalseThenReturnFalse() {
		when(pathMatcher.match(pattern, path)).thenReturn(false);

		assertThat(matcher.matches(exchange)).isFalse();

		verify(pathMatcher).match(pattern, path);
	}

	@Test
	public void matchesWhenPathMatcherTrueAndMethodTrueThenReturnTrue() {
		matcher = new PathMatcherServerWebExchangeMatcher(pattern, exchange.getRequest().getMethod());
		matcher.setPathMatcher(pathMatcher);
		when(pathMatcher.match(pattern, path)).thenReturn(true);

		assertThat(matcher.matches(exchange)).isTrue();
	}

	@Test
	public void matchesWhenPathMatcherTrueAndMethodFalseThenReturnFalse() {
		HttpMethod method = HttpMethod.OPTIONS;
		assertThat(exchange.getRequest().getMethod()).isNotEqualTo(method);
		matcher = new PathMatcherServerWebExchangeMatcher(pattern, method);
		matcher.setPathMatcher(pathMatcher);

		assertThat(matcher.matches(exchange)).isFalse();

		verifyZeroInteractions(pathMatcher);
	}

	@Test(expected = IllegalArgumentException.class)
	public void setPathMatcherWhenNullThenThrowException() {
		matcher.setPathMatcher(null);
	}
}
