package kd.mgproj.domain.policy.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import kd.mgproj.domain.policy.entity.PoliciesEntity;
import kd.mgproj.domain.policy.entity.PolicyConditionsEntity;

public interface PolicyConditionsRepository extends JpaRepository<PolicyConditionsEntity, Long> {
	Optional<PolicyConditionsEntity> findByPolicy(PoliciesEntity policy);

	List<PolicyConditionsEntity> findAllByPolicyIn(Collection<PoliciesEntity> policies);

	Optional<PolicyConditionsEntity> findByPolicyId(Long policyId);
}
