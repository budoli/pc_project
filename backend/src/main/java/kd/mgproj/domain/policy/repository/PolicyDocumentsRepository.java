package kd.mgproj.domain.policy.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import kd.mgproj.domain.policy.entity.PoliciesEntity;
import kd.mgproj.domain.policy.entity.PolicyDocumentsEntity;

public interface PolicyDocumentsRepository extends JpaRepository<PolicyDocumentsEntity, Long> {

	Optional<PolicyDocumentsEntity> findByPolicy(PoliciesEntity policy);

	List<PolicyDocumentsEntity> findAllByPolicyIn(Collection<PoliciesEntity> policies);

	Optional<PolicyDocumentsEntity> findByPolicyId(Long policyId);
}
