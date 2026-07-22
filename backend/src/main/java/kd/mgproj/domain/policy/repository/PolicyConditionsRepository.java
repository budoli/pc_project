package kd.mgproj.domain.policy.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import kd.mgproj.domain.policy.entity.PoliciesEntity;
import kd.mgproj.domain.policy.entity.PolicyConditionsEntity;

public interface PolicyConditionsRepository extends JpaRepository<PolicyConditionsEntity, Long> {
	Optional<PolicyConditionsEntity> findByPolicy(PoliciesEntity policy);

	Optional<PolicyConditionsEntity> findByPolicyId(Long policyId);
}
