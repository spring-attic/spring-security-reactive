package reactive.client;

import java.nio.charset.Charset;
import java.util.Base64;

import org.springframework.web.client.reactive.DefaultHttpRequestBuilder;
import org.springframework.web.client.reactive.RequestPostProcessor;

public class SecurityPostProcessors {

	public static RequestPostProcessor httpBasic(String user, String password) {
		return new RequestPostProcessor() {

			@Override
			public void postProcess(DefaultHttpRequestBuilder toPostProcess) {
				toPostProcess.header("Authorization", authorization(user,password));
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
