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

import static org.springframework.security.config.web.server.HttpSecurity.http;
import static org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers.*;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UserDetailsAuthenticationManager;
import org.springframework.security.config.web.server.HttpSecurity;
import org.springframework.security.web.reactive.result.method.AuthenticationPrincipalArgumentResolver;
import org.springframework.web.server.WebFilter;

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
	WebFilter springSecurityFilterChain(ReactiveAuthenticationManager manager) throws Exception {
		HttpSecurity http = http();
		// FIXME use BeanPostProcessor to set the manager
		http.authenticationManager(manager);
		http.httpBasic();
		http.authorizeRequests()
				.matchers(antMatchers("/admin/**")).hasRole("ADMIN")
				.matchers(anyExchange()).authenticated();
		return http.build();
	}

	@Bean
	public ReactiveAuthenticationManager authenticationManager(UserRepositoryUserDetailsRepository udr) {
		return new UserDetailsAuthenticationManager(udr);
	}
}