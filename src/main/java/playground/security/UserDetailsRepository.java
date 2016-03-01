package playground.security;

import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import reactor.core.publisher.Mono;

public class UserDetailsRepository {

	public Mono<UserDetails> findByUsername(String username) {
		if("rob".equals(username)) {
			return Mono.just(new User(username, username, AuthorityUtils.createAuthorityList("ROLE_USER")));
		}
		return Mono.empty();
	}
}
