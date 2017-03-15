/*
 * Copyright 2002-2016 the original author or authors.
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
package org.springframework.security.config.web.server;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.web.server.SecurityContextRepositoryWebFilter;
import org.springframework.security.web.server.WebFilterChainFilter;
import org.springframework.security.web.server.context.WebSessionSecurityContextRepository;
import org.springframework.web.server.WebFilter;

public class HttpSecurity {
	private AuthorizeRequestBuilder authorizeRequest;

	private HeaderBuilder headers = new HeaderBuilder();
	private HttpBasicBuilder httpBasic;
	private ReactiveAuthenticationManager authenticationManager;

	private WebSessionSecurityContextRepository securityContextRepository = new WebSessionSecurityContextRepository();

	public HttpBasicBuilder httpBasic() {
		if(httpBasic == null) {
			httpBasic = new HttpBasicBuilder();
		}
		return httpBasic;
	}

	public HeaderBuilder headers() {
		if(headers == null) {
			headers = new HeaderBuilder();
		}
		return headers;
	}

	public AuthorizeRequestBuilder authorizeRequests() {
		if(authorizeRequest == null) {
			authorizeRequest = new AuthorizeRequestBuilder();
		}
		return authorizeRequest;
	}

	public HttpSecurity authenticationManager(ReactiveAuthenticationManager manager) {
		this.authenticationManager = manager;
		return this;
	}

	public WebFilter build() {
		List<WebFilter> filters = new ArrayList<>();
		if(headers != null) {
			filters.add(headers.build());
		}
		filters.add(securityContextRepositoryWebFilter());
		if(httpBasic != null) {
			httpBasic.authenticationManager(authenticationManager);
			filters.add(httpBasic.build());
		}
		if(authorizeRequest != null) {
			filters.add(authorizeRequest.build());
		}
		return new WebFilterChainFilter(filters);
	}

	public static HttpSecurity http() {
		return new HttpSecurity();
	}

	private SecurityContextRepositoryWebFilter securityContextRepositoryWebFilter() {
		return new SecurityContextRepositoryWebFilter(securityContextRepository);
	}


	private HttpSecurity() {}
}