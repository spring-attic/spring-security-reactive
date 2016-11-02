/*
 * Copyright 2002-2016 the original author or authors.
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
package org.springframework.security.authentication;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;

import reactor.core.publisher.Mono;

@RunWith(MockitoJUnitRunner.class)
public class UserDetailsAuthenticationManagerTests {
	@Mock
	UserDetailsRepository repository;
	UserDetailsAuthenticationManager manager;
	String username;
	String password;

	@Before
	public void setup() {
		manager = new UserDetailsAuthenticationManager(repository);
		username = "user";
		password = "pass";
	}

	@Test
	public void authenticateWhenUserNotFoundThenNull() {
		when(repository.findByUsername(username)).thenReturn(Mono.empty());

		UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username, password);
		Authentication authentication = manager.authenticate(token).block();

		assertThat(authentication).isNull();
	}

	@Test
	public void authenticateWhenPasswordNotEqualThenNull() {
		User user = new User(username, password, AuthorityUtils.createAuthorityList("ROLE_USER"));
		when(repository.findByUsername(user.getUsername())).thenReturn(Mono.just(user));

		UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username, password + "INVALID");
		Authentication authentication = manager.authenticate(token).block();

		assertThat(authentication).isNull();
	}

}
