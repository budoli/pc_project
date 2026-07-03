package kd.mgproj.domain.policy.repository;

import kd.mgproj.domain.policy.entity.PoliciesEntity;
import kd.mgproj.domain.policy.entity.PolicyDocumentsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PolicyDocumentsRepository extends JpaRepository<PolicyDocumentsEntity, Long> {

    Optional<PolicyDocumentsEntity> findByPolicy(PoliciesEntity policy);
}
