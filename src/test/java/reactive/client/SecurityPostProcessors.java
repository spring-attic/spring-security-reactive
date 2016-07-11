package reactive.client;

import java.nio.charset.Charset;
import java.util.Base64;

import org.springframework.web.client.reactive.ClientWebRequest;
import org.springframework.web.client.reactive.ClientWebRequestPostProcessor;

public class SecurityPostProcessors {

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
