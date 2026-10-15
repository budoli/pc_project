package kd.mgproj.domain.policy.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import kd.mgproj.domain.policy.service.PolicySyncService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/policies")
public class AdminPolicyController {
	private final PolicySyncService policySyncService;

	@PostMapping("/sync")
	public ResponseEntity<Integer> syncPolicies() {
		int count = policySyncService.syncPolicies();
		return ResponseEntity.ok(count);
	}

	@PostMapping("/reindex")
	public ResponseEntity<Integer> reindexPolicies() {
		int count = policySyncService.reindexPolicies();
		return ResponseEntity.ok(count);
	}

}
