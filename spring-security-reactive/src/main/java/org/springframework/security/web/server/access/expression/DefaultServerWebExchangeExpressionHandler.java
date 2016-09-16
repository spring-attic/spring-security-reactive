package org.springframework.security.web.server.access.expression;

import org.springframework.security.access.expression.AbstractSecurityExpressionHandler;
import org.springframework.security.access.expression.SecurityExpressionOperations;
import org.springframework.security.access.expression.SecurityExpressionRoot;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.authentication.AuthenticationTrustResolverImpl;
import org.springframework.security.core.Authentication;
import org.springframework.web.server.ServerWebExchange;

public class DefaultServerWebExchangeExpressionHandler extends AbstractSecurityExpressionHandler<ServerWebExchange> {
	private String defaultRolePrefix = "ROLE_";
	private AuthenticationTrustResolver trustResolver = new AuthenticationTrustResolverImpl();

	@Override
	protected SecurityExpressionOperations createSecurityExpressionRoot(Authentication authentication,
			ServerWebExchange invocation) {
		SecurityExpressionRoot root = new SecurityExpressionRoot(authentication) {};
		root.setPermissionEvaluator(getPermissionEvaluator());
		root.setTrustResolver(trustResolver);
		root.setRoleHierarchy(getRoleHierarchy());
		root.setDefaultRolePrefix(defaultRolePrefix);
		return root;
	}

}
