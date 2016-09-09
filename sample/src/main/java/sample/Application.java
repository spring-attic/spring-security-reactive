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

import java.util.Arrays;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.reactiveweb.ReactiveWebAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.access.ReactiveAccessDecisionManagerAdapter;
import org.springframework.security.access.vote.RoleVoter;
import org.springframework.security.access.vote.UnanimousBased;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.ReactiveAuthenticationManagerAdapter;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.reactive.result.method.AuthenticationPrincipalArgumentResolver;
import org.springframework.security.web.server.AuthenticationEntryPoint;
import org.springframework.security.web.server.AuthenticationWebFilter;
import org.springframework.security.web.server.AuthorizationWebFilter;
import org.springframework.security.web.server.HttpBasicAuthenticationFactory;
import org.springframework.security.web.server.authentication.www.HttpBasicAuthenticationEntryPoint;
import org.springframework.security.web.server.context.WebSessionSecurityContextRepository;

/**
 * @author Rob Winch
 */
@SpringBootApplication(exclude = ReactiveWebAutoConfiguration.class)
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
		UnanimousBased authz = new UnanimousBased(Arrays.asList(new RoleVoter()));
		return new AuthorizationWebFilter(new ReactiveAccessDecisionManagerAdapter(authz));
	}

	@Bean
	public AuthenticationWebFilter authenticationFilter() {
		AuthenticationWebFilter authenticationFilter = new AuthenticationWebFilter();
		authenticationFilter.setAuthenticationManager(authenticationManager());
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
	public ReactiveAuthenticationManager authenticationManager() {
		User rob = new User("rob", "rob", AuthorityUtils.createAuthorityList("ROLE_USER"));
		User admin = new User("admin", "admin", AuthorityUtils.createAuthorityList("ROLE_ADMIN", "ROLE_USER"));
		InMemoryUserDetailsManager userDetailsService = new InMemoryUserDetailsManager(Arrays.asList(admin, rob));
		DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
		authenticationProvider.setUserDetailsService(userDetailsService);
		ProviderManager authenticationManager = new ProviderManager(Arrays.asList(authenticationProvider));
		return new ReactiveAuthenticationManagerAdapter(authenticationManager);
	}

}
