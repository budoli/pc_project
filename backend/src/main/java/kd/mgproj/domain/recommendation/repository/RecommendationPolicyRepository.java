package kd.mgproj.domain.recommendation.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import kd.mgproj.domain.recommendation.entity.RecommendationEntity;
import kd.mgproj.domain.recommendation.entity.RecommendationPolicyEntity;

public interface RecommendationPolicyRepository extends JpaRepository<RecommendationPolicyEntity, Long> {
	List<RecommendationPolicyEntity> findByRecommendation(RecommendationEntity recommendation);
}
