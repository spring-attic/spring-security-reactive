/*
 * Copyright 2016 the original author or authors.
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

import static org.assertj.core.api.Assertions.assertThat;
//import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Integration tests showing the usage of Reactive MongoDB support using mixed reactive types through Spring Data
 * repositories.
 *
 * @author Mark Paluch
 */
@SuppressWarnings("unused")
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = Application.class)
@TestPropertySource(properties = "server.port=0")
public class UserRepositoryTests {

	@Autowired UserRepository repository;

	User rob;

	@Before
	public void setUp() {
		this.rob = repository.save(new User("user", "password", "First", "Last")).block();
	}

	@Test
	public void findByUsernameWhenUsernameMatchesThenFound() {
		assertThat(repository.findByUsername(this.rob.getUsername()).block()).isNotNull();
	}

	@Test
	public void findByUsernameWhenUsernameDoesNotMatchThenFound() {
		assertThat(repository.findByUsername(this.rob.getUsername() + "NOTFOUND").block()).isNull();
	}
}
