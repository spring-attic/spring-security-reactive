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
