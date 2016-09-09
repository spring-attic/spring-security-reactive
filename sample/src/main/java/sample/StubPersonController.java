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

import java.util.Collections;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author Rob Winch
 */
@RestController
public class StubPersonController {

	private final ReactiveRepository<Person> repository;

	@Autowired
	public StubPersonController(StubPersonRepository repository) {
		this.repository = repository;
	}

	@RequestMapping(path="/people", method = RequestMethod.POST)
	public Mono<Void> create(@RequestBody Flux<Person> personStream) {
		return this.repository.insert(personStream);
	}

	@RequestMapping(path="/people", method = RequestMethod.GET)
	public Flux<Person> list() {
		return this.repository.list();
	}

	// TODO Manage {@code @PathVariable}
	@RequestMapping(path = "/people/1", method = RequestMethod.GET)
	public Mono<Person> findById() {
		return this.repository.findById("1");
	}

	@RequestMapping("/me")
	public Map<String,String> me(@AuthenticationPrincipal UserDetails user) {
		return Collections.singletonMap("username", user.getUsername());
	}

	@RequestMapping("/admin")
	public Map<String,String> admin() {
		return Collections.singletonMap("isadmin", "true");
	}
}
