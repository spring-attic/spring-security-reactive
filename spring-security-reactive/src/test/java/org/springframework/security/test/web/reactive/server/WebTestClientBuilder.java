/*
 * Copyright 2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.security.test.web.reactive.server;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.reactive.server.WebTestClient.Builder;
import org.springframework.web.reactive.DispatcherHandler;
import org.springframework.web.reactive.HandlerAdapter;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.HandlerResultHandler;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.reactive.function.server.support.HandlerFunctionAdapter;
import org.springframework.web.reactive.function.server.support.ServerResponseResultHandler;
import org.springframework.web.server.WebFilter;

import reactor.core.publisher.Mono;

/**
 * Provides a convenient mechanism for running {@link WebTestClient} against
 * {@link WebFilter} until https://jira.spring.io/browse/SPR-15349 is resolved
 *
 * @author Rob Winch
 * @since 5.0
 *
 */
public class WebTestClientBuilder {

	public static Builder bindToWebFilters(WebFilter... webFilters) {
		return WebTestClient.bindToApplicationContext(applicationContext(webFilters)).configureClient();
	}

	private static ApplicationContext applicationContext(WebFilter... webFilters) {
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
		for(WebFilter filter : webFilters) {
			context.registerBean(WebFilter.class, () -> filter);
		}
		context.registerBean("webHandler", DispatcherHandler.class, () -> new DispatcherHandler());
		context.registerBean(HandlerMapping.class, () -> RouterFunctions.toHandlerMapping(request -> Mono.just(r -> ServerResponse.ok().build())));
		context.registerBean(HandlerAdapter.class, () -> new HandlerFunctionAdapter());
		context.registerBean(HandlerResultHandler.class, () -> new ServerResponseResultHandler());
		context.refresh();

		return context;
	}
}
