package playground.security;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.core.Authentication;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.util.function.*;

public class RxAccessDecisionManagerAdapter implements RxAccessDecisionManager {
	private final AccessDecisionManager accessDecisionManager;

	public RxAccessDecisionManagerAdapter(AccessDecisionManager accessDecisionManager) {
		super();
		this.accessDecisionManager = accessDecisionManager;
	}

	public Mono<Boolean> decide(Authentication authentication, Object object, Flux<ConfigAttribute> configAttributes) {
		return Mono
			.just(Tuples.of(authentication, object, configAttributes))
			.publishOn(Schedulers.elastic())
			.filter((Tuple3<Authentication, Object, Flux<ConfigAttribute>> t) -> {
				Authentication auth = t.t1;
				return auth != null && auth.isAuthenticated();
			})
			.then((Function<Tuple3<Authentication, Object, Flux<ConfigAttribute>>, Mono<Boolean>>) t -> {
				List<ConfigAttribute> attrs = new ArrayList<>();
				t.t3.toIterable().forEach(attrs::add);

				try {
					accessDecisionManager.decide(t.t1, t.t2, attrs);
					return Mono.just(true);
				} catch(AccessDeniedException fail) {
					return Mono.just(false);
				}
			});
	}
}