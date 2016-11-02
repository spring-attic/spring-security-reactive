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

import java.security.Principal;
import java.util.Collections;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Flux;

/**
 * @author Rob Winch
 */
@RestController
public class UserController {

	private final UserRepository users;

	@Autowired
	public UserController(UserRepository users) {
		this.users = users;
	}

	@PostMapping(path="/user")
	public Flux<User> create(@RequestBody Flux<User> userStream) {
		return this.users.save(userStream);
	}

	@GetMapping(path="/users")
	public Flux<User> list() {
		return this.users.findAll();
	}

	@RequestMapping("/me")
	public Map<String,String> me(@AuthenticationPrincipal UserDetails user) {
		return Collections.singletonMap("username", user.getUsername());
	}

	@RequestMapping("/principal")
	public Map<String,String> principal(Principal principal) {
		return Collections.singletonMap("username", principal.getName());
	}

	@RequestMapping("/admin")
	public Map<String,String> admin() {
		return Collections.singletonMap("isadmin", "true");
	}
}
