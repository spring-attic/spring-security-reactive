package reactive.client;

import java.nio.charset.Charset;
import java.util.Base64;

public class SecurityPostProcessors {

	public static RequestPostProcessor httpBasic(String user, String password) {
		return new RequestPostProcessor() {

			@Override
			public ExtendedDefaultHttpRequestBuilder postProcess(ExtendedDefaultHttpRequestBuilder request) {
				request.header("Authorization", authorization(user,password));
				return request;
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
