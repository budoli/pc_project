package kd.mgproj.domain.recommendation.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import kd.mgproj.domain.recommendation.entity.RecommendationEntity;

public interface RecommendationRepository extends JpaRepository<RecommendationEntity, Long> {
	List<RecommendationEntity> findByUserIdOrderByCreatedAtDesc(Long userId);

	Optional<RecommendationEntity> findByIdAndUserId(Long recommendationId, Long userId);
}
