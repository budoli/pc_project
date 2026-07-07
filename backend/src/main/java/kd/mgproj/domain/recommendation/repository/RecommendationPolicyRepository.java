package kd.mgproj.domain.recommendation.repository;

import kd.mgproj.domain.recommendation.entity.RecommendationEntity;
import kd.mgproj.domain.recommendation.entity.RecommendationPolicyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecommendationPolicyRepository extends JpaRepository<RecommendationPolicyEntity, Long> {
    List<RecommendationPolicyEntity> findByRecommendation(RecommendationEntity recommendation);
}
