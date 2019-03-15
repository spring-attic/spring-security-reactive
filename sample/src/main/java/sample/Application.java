/*
 * Copyright 2002-2015 the original author or authors.
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

package sample;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.reactive.HttpHandler;
import org.springframework.http.server.reactive.ReactorHttpHandlerAdapter;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UserDetailsAuthenticationManager;
import org.springframework.security.config.web.server.AuthorizeRequestBuilder;
import org.springframework.security.config.web.server.HttpSecurity;
import org.springframework.security.web.reactive.result.method.AuthenticationPrincipalArgumentResolver;
import org.springframework.web.reactive.DispatcherHandler;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import org.springframework.web.reactive.result.method.annotation.ArgumentResolverConfigurer;
import org.springframework.web.server.WebFilter;
import reactor.ipc.netty.NettyContext;
import reactor.ipc.netty.http.server.HttpServer;

import static org.springframework.security.config.web.server.HttpSecurity.http;

/**
 * @author Rob Winch
 */
@Configuration
@EnableWebFlux
@ComponentScan
public class Application implements WebFluxConfigurer {
	@Value("${server.port:8080}")
	private int port = 8080;

	public static void main(String[] args) throws Exception {
		try(AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(Application.class)) {
			context.getBean(NettyContext.class).onClose().block();
		}
	}

	@Override
	public void configureArgumentResolvers(ArgumentResolverConfigurer configurer) {
		configurer.addCustomResolver(authenticationPrincipalArgumentResolver());
	}

	@Bean
	public NettyContext nettyContext(ApplicationContext context) {
		HttpHandler handler = DispatcherHandler.toHttpHandler(context);
		ReactorHttpHandlerAdapter adapter = new ReactorHttpHandlerAdapter(handler);
		HttpServer httpServer = HttpServer.create("localhost", port);
		return httpServer.newHandler(adapter).block();
	}

	@Bean
	public AuthenticationPrincipalArgumentResolver authenticationPrincipalArgumentResolver() {
		return new AuthenticationPrincipalArgumentResolver();
	}

	@Bean
	WebFilter springSecurityFilterChain(ReactiveAuthenticationManager manager) throws Exception {
		HttpSecurity http = http();
		// FIXME use BeanPostProcessor to set the manager
		http.authenticationManager(manager);
		http.httpBasic();

		AuthorizeRequestBuilder authorize = http.authorizeRequests();
		authorize.antMatchers("/admin/**").hasRole("ADMIN");
		authorize.anyExchange().authenticated();
		return http.build();
	}

	@Bean
	public ReactiveAuthenticationManager authenticationManager(UserRepositoryUserDetailsRepository udr) {
		return new UserDetailsAuthenticationManager(udr);
	}
}