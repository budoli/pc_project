package kd.mgproj.domain.policy.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import kd.mgproj.domain.policy.dto.response.AdminPolicyStatsResponse;
import kd.mgproj.domain.policy.dto.response.PolicySyncResponse;
import kd.mgproj.domain.policy.service.PolicyStatsService;
import kd.mgproj.domain.policy.service.PolicySyncService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/policies")
public class AdminPolicyController {
	private final PolicySyncService policySyncService;
	private final PolicyStatsService policyStatsService;

	@GetMapping("/stats")
	public ResponseEntity<AdminPolicyStatsResponse> getStats() {
		return ResponseEntity.ok(policyStatsService.getStats());
	}

	@PostMapping("/sync")
	public ResponseEntity<PolicySyncResponse> syncPolicies() {
		return ResponseEntity.ok(policySyncService.syncPolicies());
	}

	@PostMapping("/reindex")
	public ResponseEntity<Integer> reindexPolicies() {
		int count = policySyncService.reindexPolicies();
		return ResponseEntity.ok(count);
	}

}
