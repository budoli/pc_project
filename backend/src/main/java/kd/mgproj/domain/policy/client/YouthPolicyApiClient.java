package kd.mgproj.domain.policy.client;

import kd.mgproj.domain.policy.dto.YouthPolicyItem;
import kd.mgproj.domain.policy.dto.response.YouthPolicyApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
public class YouthPolicyApiClient {
    @Value("${youth.api.key}")
    private String apiKey;

    @Value("${youth.api.base-url}")
    private String baseUrl;

    private final RestClient restClient = RestClient.create();

    public YouthPolicyApiResponse fetchPolicies(int pageNum, int pageSize) {
        return restClient.get()
                .uri(baseUrl + "/go/ythip/getPlcy?apiKeyNm={apiKey}&pageNum={pageNum}&pageSize={pageSize}&pageType=1&rtnType=json",
                        apiKey, pageNum, pageSize)
                .retrieve()
                .body(YouthPolicyApiResponse.class);
    }
}
