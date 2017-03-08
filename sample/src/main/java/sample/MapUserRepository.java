package sample;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class MapUserRepository implements UserRepository {
	private final Map<String,User> users = new HashMap<>();

	public MapUserRepository() {
		save(new User("rob", "rob", "Rob", "Winch")).block();
		save(new User("admin", "admin", "Admin", "User")).block();
	}

	@Override
	public Flux<User> findAll() {
		return Flux.fromIterable(users.values());
	}

	@Override
	public Mono<User> findByUsername(String username) {
		User result = users.get(username);

		return result == null ? Mono.empty() : Mono.just(result);
	}

	public Mono<User> save(User user) {
		users.put(user.getUsername(), user);
		return Mono.just(user);
	}
}
