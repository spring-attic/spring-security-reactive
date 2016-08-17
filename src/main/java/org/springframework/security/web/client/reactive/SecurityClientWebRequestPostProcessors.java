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
package org.springframework.security.web.client.reactive;

import java.nio.charset.Charset;
import java.util.Base64;

import org.springframework.web.client.reactive.ClientWebRequest;
import org.springframework.web.client.reactive.ClientWebRequestPostProcessor;

/**
 * 
 * @author Rob Winch
 * @since 5.0
 */
public class SecurityClientWebRequestPostProcessors {

	public static ClientWebRequestPostProcessor httpBasic(String user, String password) {
		return new ClientWebRequestPostProcessor() {

			@Override
			public ClientWebRequest postProcess(ClientWebRequest toPostProcess) {
				toPostProcess.getHttpHeaders().set("Authorization", authorization(user,password));
				return toPostProcess;
			}

			private String authorization(String username, String password) {
				String credentials = username+":"+password;
				return authorization(credentials);
			}

			private String authorization(String credentials) {
				String encodedCredentials =  Base64.getEncoder().encodeToString(credentials.getBytes(Charset.defaultCharset()));
				return "Basic " + encodedCredentials;
			}

		};
	}
}
