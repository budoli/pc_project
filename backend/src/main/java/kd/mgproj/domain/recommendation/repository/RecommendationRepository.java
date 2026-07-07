package kd.mgproj.domain.recommendation.repository;

import kd.mgproj.domain.recommendation.entity.RecommendationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RecommendationRepository extends JpaRepository<RecommendationEntity, Long> {
    List<RecommendationEntity> findByUserIdOrderByCreatedAtDesc(Long userId);

    Optional<RecommendationEntity> findByIdAndUserId(Long recommendationId, Long userId);
}
