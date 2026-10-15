package kd.mgproj.domain.policy.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import kd.mgproj.domain.policy.entity.FavoritePolicyEntity;

public interface FavoritePolicyRepository extends JpaRepository<FavoritePolicyEntity, Long> {
    List<FavoritePolicyEntity> findByUserIdOrderByCreatedAtDesc(Long userId);

    boolean existsByUserIdAndPolicyId(Long userId, Long policyId);

    void deleteByUserIdAndPolicyId(Long userId, Long policyId);
}
