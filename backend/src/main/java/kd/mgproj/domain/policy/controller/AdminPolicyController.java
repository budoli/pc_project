package kd.mgproj.domain.policy.controller;

import kd.mgproj.domain.policy.service.PolicySyncService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

}
