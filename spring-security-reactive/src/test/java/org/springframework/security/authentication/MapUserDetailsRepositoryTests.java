/*
 * Copyright 2017 the original author or authors.
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

import java.util.Collections;

import org.junit.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import reactor.core.publisher.Mono;

public class MapUserDetailsRepositoryTests {
	private static final UserDetails USER_DETAILS = User.withUsername("user")
			.password("password")
			.roles("USER")
			.build();

	private MapUserDetailsRepository users = new MapUserDetailsRepository(Collections.singletonMap(USER_DETAILS.getUsername(), USER_DETAILS));

	@Test
	public void findByUsernameWhenFoundThenReturns() {
		assertThat((users.findByUsername(USER_DETAILS.getUsername()).block())).isEqualTo(USER_DETAILS);
	}

	@Test
	public void findByUsernameWhenNotFoundThenEmpty() {
		assertThat((users.findByUsername("notfound"))).isEqualTo(Mono.empty());
	}
}
