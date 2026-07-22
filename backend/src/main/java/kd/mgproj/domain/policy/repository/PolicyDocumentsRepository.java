package kd.mgproj.domain.policy.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import kd.mgproj.domain.policy.entity.PoliciesEntity;
import kd.mgproj.domain.policy.entity.PolicyDocumentsEntity;

public interface PolicyDocumentsRepository extends JpaRepository<PolicyDocumentsEntity, Long> {

	Optional<PolicyDocumentsEntity> findByPolicy(PoliciesEntity policy);

	Optional<PolicyDocumentsEntity> findByPolicyId(Long policyId);
}
