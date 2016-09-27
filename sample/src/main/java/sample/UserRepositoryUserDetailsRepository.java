package sample;

import java.util.Collection;
import java.util.List;

import org.springframework.security.authentication.UserDetailsRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import reactor.core.publisher.Mono;

@Component
public class UserRepositoryUserDetailsRepository implements UserDetailsRepository {
	private final UserRepository users;

	public UserRepositoryUserDetailsRepository(UserRepository users) {
		super();
		this.users = users;
	}

	@Override
	public Mono<UserDetails> findByUsername(String username) {
		return this.users
				.findByUsername(username)
				.map(UserDetailsAdapter::new);
	}

	@SuppressWarnings("serial")
	private static class UserDetailsAdapter extends User implements UserDetails {
		private static List<GrantedAuthority> USER_ROLES = AuthorityUtils.createAuthorityList("ROLE_USER");
		private static List<GrantedAuthority> ADMIN_ROLES = AuthorityUtils.createAuthorityList("ROLE_ADMIN", "ROLE_USER");

		private UserDetailsAdapter(User delegate) {
			super(delegate);
		}

		@Override
		public Collection<? extends GrantedAuthority> getAuthorities() {
			return isAdmin() ? ADMIN_ROLES : USER_ROLES ;
		}

		private boolean isAdmin() {
			return getUsername().contains("admin");
		}

		@Override
		public boolean isAccountNonExpired() {
			return true;
		}

		@Override
		public boolean isAccountNonLocked() {
			return true;
		}

		@Override
		public boolean isCredentialsNonExpired() {
			return true;
		}

		@Override
		public boolean isEnabled() {
			return true;
		}
	}
}
