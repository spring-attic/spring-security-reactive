/*
 * Copyright 2002-2015 the original author or authors.
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

import static org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers.antMatchers;
import static org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers.anyExchange;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UserDetailsAuthenticationManager;
import org.springframework.security.web.reactive.result.method.AuthenticationPrincipalArgumentResolver;
import org.springframework.security.web.server.AuthenticationEntryPoint;
import org.springframework.security.web.server.AuthenticationWebFilter;
import org.springframework.security.web.server.AuthorizationWebFilter;
import org.springframework.security.web.server.HttpBasicAuthenticationFactory;
import org.springframework.security.web.server.SecurityContextRepositoryWebFilter;
import org.springframework.security.web.server.access.expression.ExpressionReactiveAccessDecisionManager;
import org.springframework.security.web.server.access.expression.ServerWebExchangeMetadataSource;
import org.springframework.security.web.server.authentication.www.HttpBasicAuthenticationEntryPoint;
import org.springframework.security.web.server.context.WebSessionSecurityContextRepository;

/**
 * @author Rob Winch
 */
@SpringBootApplication
public class Application {

	public static void main(String[] args) throws Exception {
		SpringApplication.run(Application.class, args);
	}

	@Bean
	public AuthenticationPrincipalArgumentResolver authenticationPrincipalArgumentResolver() {
		return new AuthenticationPrincipalArgumentResolver();
	}

	@Bean
	public AuthorizationWebFilter authorizationFilter() {
		ExpressionReactiveAccessDecisionManager manager = new ExpressionReactiveAccessDecisionManager();
		ServerWebExchangeMetadataSource metadataSource = ServerWebExchangeMetadataSource
				.builder()
				.add(antMatchers("/admin/**"), new SecurityConfig("hasRole('ADMIN')"))
				.add(anyExchange(), new SecurityConfig("authenticated"))
				.build();
		return new AuthorizationWebFilter(manager, metadataSource );
	}

	@Bean
	public SecurityContextRepositoryWebFilter securityContextRepositoryWebFilter() {
		return new SecurityContextRepositoryWebFilter(securityContextRepository());
	}

	@Bean
	public AuthenticationWebFilter authenticationFilter(ReactiveAuthenticationManager authenticationManager) {
		AuthenticationWebFilter authenticationFilter = new AuthenticationWebFilter();
		authenticationFilter.setAuthenticationManager(authenticationManager);
		authenticationFilter.setEntryPoint(entryPoint());
		authenticationFilter.setAuthenticationConverter(new HttpBasicAuthenticationFactory());
		authenticationFilter.setSecurityContextRepository(securityContextRepository());
		return authenticationFilter;
	}

	@Bean
	public WebSessionSecurityContextRepository securityContextRepository() {
		return new WebSessionSecurityContextRepository();
	}

	@Bean
	public AuthenticationEntryPoint entryPoint() {
		return new HttpBasicAuthenticationEntryPoint();
	}

	@Bean
	public ReactiveAuthenticationManager authenticationManager(UserRepositoryUserDetailsRepository udr) {
		return new UserDetailsAuthenticationManager(udr);
	}

}
