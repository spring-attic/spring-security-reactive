package sample;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertySource;
import org.springframework.util.SocketUtils;

public class RandomPortPostProcessor  implements EnvironmentPostProcessor {
	private Map<String,Integer> ports = new HashMap<>();

	@Override
	public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
		environment.getPropertySources().addFirst(new PropertySource<Object>("random") {

			@Override
			public Object getProperty(String name) {
				if(!name.startsWith("random.tcpport.")) {
					return null;
				}
				Integer port = ports.get(name);
				if(port == null) {
					port = SocketUtils.findAvailableTcpPort();
					ports.put(name, port);
				}
				return port;
			}});
	}
}