package kd.mgproj.domain.recommendation.repository;

import kd.mgproj.domain.recommendation.entity.RecommendationPolicyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecommendationPolicyRepository extends JpaRepository<RecommendationPolicyEntity, Long> {
}
