/*
 * Copyright 2002-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.security.config.web.server;

import org.springframework.security.web.server.CacheControlHttpHeadersWriter;
import org.springframework.security.web.server.CompositeHttpHeadersWriter;
import org.springframework.security.web.server.ContentTypeOptionsHttpHeadersWriter;
import org.springframework.security.web.server.HttpHeaderWriterWebFilter;
import org.springframework.security.web.server.HttpHeadersWriter;
import org.springframework.security.web.server.StrictTransportSecurityHttpHeadersWriter;
import org.springframework.security.web.server.XFrameOptionsHttpHeadersWriter;
import org.springframework.security.web.server.header.XXssProtectionHttpHeadersWriter;

public class HeaderBuilder {
	private CacheControlHttpHeadersWriter cacheControl = new CacheControlHttpHeadersWriter();

	private ContentTypeOptionsHttpHeadersWriter contentType = new ContentTypeOptionsHttpHeadersWriter();

	private StrictTransportSecurityHttpHeadersWriter hsts = new StrictTransportSecurityHttpHeadersWriter();

	private XFrameOptionsHttpHeadersWriter frameOptions = new XFrameOptionsHttpHeadersWriter();

	private XXssProtectionHttpHeadersWriter xss = new XXssProtectionHttpHeadersWriter();

	public HttpHeaderWriterWebFilter build() {
		HttpHeadersWriter writer = new CompositeHttpHeadersWriter(cacheControl, contentType, hsts, frameOptions , xss);
		return new HttpHeaderWriterWebFilter(writer);
	}
}
