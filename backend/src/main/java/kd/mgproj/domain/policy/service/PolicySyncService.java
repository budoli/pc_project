package kd.mgproj.domain.policy.service;

import kd.mgproj.domain.policy.dto.response.PolicySyncResponse;

public interface PolicySyncService {
	PolicySyncResponse syncPolicies();

	int reindexPolicies();
}
