package kd.mgproj.domain.policy.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import kd.mgproj.domain.policy.dto.response.YouthPolicyApiResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class YouthPolicyApiClient {
	private final RestClient restClient = RestClient.create();
	@Value("${youth.api.key}")
	private String apiKey;
	@Value("${youth.api.base-url}")
	private String baseUrl;

	public YouthPolicyApiResponse fetchPolicies(int pageNum, int pageSize) {
		return restClient.get()
			.uri(baseUrl
					+ "/go/ythip/getPlcy?apiKeyNm={apiKey}&pageNum={pageNum}&pageSize={pageSize}&pageType=1&rtnType=json",
				apiKey, pageNum, pageSize)
			.retrieve()
			.body(YouthPolicyApiResponse.class);
	}
}
