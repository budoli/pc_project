package kd.mgproj.domain.policy.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import kd.mgproj.domain.policy.dto.response.YouthPolicyApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class YouthPolicyApiClient {
	// 온통청년 API 는 연속 호출 시 간헐적으로 400 을 돌려주고, 같은 요청을 다시 보내면 성공한다
	private static final int MAX_ATTEMPTS = 3;
	private static final long INITIAL_BACKOFF_MS = 500;

	private final RestClient restClient = RestClient.create();
	@Value("${youth.api.key}")
	private String apiKey;
	@Value("${youth.api.base-url}")
	private String baseUrl;

	public YouthPolicyApiResponse fetchPolicies(int pageNum, int pageSize) {
		long backoffMs = INITIAL_BACKOFF_MS;

		for (int attempt = 1; ; attempt++) {
			try {
				return restClient.get()
					.uri(baseUrl
							+ "/go/ythip/getPlcy?apiKeyNm={apiKey}&pageNum={pageNum}&pageSize={pageSize}&pageType=1&rtnType=json",
						apiKey, pageNum, pageSize)
					.retrieve()
					.body(YouthPolicyApiResponse.class);
			} catch (RestClientException exception) {
				if (attempt >= MAX_ATTEMPTS) {
					throw exception;
				}
				log.warn("Youth policy API failed (page {}, attempt {}/{}): {}",
					pageNum, attempt, MAX_ATTEMPTS, exception.getMessage());
				sleep(backoffMs);
				backoffMs *= 2;
			}
		}
	}

	private void sleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException exception) {
			Thread.currentThread().interrupt();
			throw new IllegalStateException("Interrupted while retrying youth policy API", exception);
		}
	}
}
