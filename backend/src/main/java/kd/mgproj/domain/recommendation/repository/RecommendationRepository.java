package kd.mgproj.domain.recommendation.repository;

import kd.mgproj.domain.recommendation.entity.RecommendationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecommendationRepository extends JpaRepository<RecommendationEntity, Long> {
}
