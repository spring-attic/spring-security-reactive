package playground.security;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class AuthorizationFilter implements WebFilter {
	HttpSessionSecurityContextRepository securityContextRepository = new HttpSessionSecurityContextRepository();

	AuthenticationEntryPoint entryPoint = new HttpBasicAuthenticationEntryPoint();

	ServerWebExchangeMetadataSource source = new ServerWebExchangeMetadataSource();

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
		return securityContextRepository.load(exchange)
			.map(SecurityContext::getAuthentication)
			.filter(authentication -> {
				return authentication != null && authentication.isAuthenticated();
			})
			.then(authentication -> {
				Set<String> authorityNames = authentication.getAuthorities().stream().map( a-> a.getAuthority()).collect(Collectors.toSet());
				Flux<ConfigAttribute> attributes = source.getConfigAttributes(exchange);
				return attributes.all( attr -> authorityNames.contains(attr.getAttribute()));
			})
			.filter(t -> t)
			.otherwiseIfEmpty(Mono.defer(() -> {
				return entryPoint.commence(exchange, new AuthenticationCredentialsNotFoundException("Not Found"));
			}))
			.then(sc -> {
				return chain.filter(exchange);
			});
	}

}
