package kd.mgproj.domain.policy.repository;

import kd.mgproj.domain.policy.entity.PoliciesEntity;
import kd.mgproj.domain.policy.entity.PolicyConditionsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PolicyConditionsRepository extends JpaRepository<PolicyConditionsEntity, Long> {
    Optional<PolicyConditionsEntity> findByPolicy(PoliciesEntity policy);

    Optional<PolicyConditionsEntity> findByPolicyId(Long policyId);
}
