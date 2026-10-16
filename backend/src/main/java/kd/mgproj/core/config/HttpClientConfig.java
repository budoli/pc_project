package kd.mgproj.core.config;

import java.time.Duration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ReactorClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
public class HttpClientConfig {

	@Bean
	public RestClient restClient() {
		ReactorClientHttpRequestFactory requestFactory =
			new ReactorClientHttpRequestFactory();
		requestFactory.setConnectTimeout(Duration.ofSeconds(10));
		requestFactory.setReadTimeout(Duration.ofMinutes(10));

		return RestClient.builder()
			.requestFactory(requestFactory)
			.build();
	}
}
