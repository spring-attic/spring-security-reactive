/*
 * Copyright 2002-2015 the original author or authors.
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

package playground.security;

import org.junit.After;
import org.junit.Before;
import org.springframework.http.server.reactive.HttpHandler;
import org.springframework.http.server.reactive.boot.RxNettyHttpServer;
import org.springframework.util.SocketUtils;
import org.springframework.web.client.RestTemplate;

public abstract class AbstractHttpHandlerIntegrationTests {

	int port;
	RxNettyHttpServer server;
	RestTemplate rest;

	@Before
	public void setup() throws Exception {
		this.port = SocketUtils.findAvailableTcpPort();
		this.server = new RxNettyHttpServer();
		this.server.setPort(this.port);
		this.server.setHandler(createHttpHandler());
		this.server.afterPropertiesSet();
		this.server.start();

		this.rest = new RestTemplate();
	}

	protected abstract HttpHandler createHttpHandler() throws Exception;

	@After
	public void tearDown() throws Exception {
		this.server.stop();
	}

}
