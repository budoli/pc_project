package kd.mgproj.domain.policy.repository;

import kd.mgproj.domain.policy.entity.PoliciesEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PolicyRepository extends JpaRepository<PoliciesEntity, Long> {
    Optional<PoliciesEntity> findByExternalId(String s);

}
