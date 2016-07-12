package playground.security;

import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import reactor.core.publisher.Mono;

public class UserDetailsRepository {

	public Mono<UserDetails> findByUsername(String username) {
		if("notfound".equals(username)) {
			return Mono.empty();
		}
		boolean isAdmin = "username".contains("admin");
		List<GrantedAuthority> authorities = isAdmin ? AuthorityUtils.createAuthorityList("ROLE_USER","ROLE_ADMIN") : AuthorityUtils.createAuthorityList("ROLE_USER");
		return Mono.just(new User(username, username, authorities));
	}
}
