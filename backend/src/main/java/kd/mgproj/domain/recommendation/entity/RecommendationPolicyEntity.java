package kd.mgproj.domain.recommendation.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import kd.mgproj.common.entity.BaseEntity;
import kd.mgproj.domain.policy.entity.PoliciesEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "recommendation_policy")
public class RecommendationPolicyEntity extends BaseEntity {
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "recommendation_id", nullable = false)
	private RecommendationEntity recommendation;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "policy_id", nullable = false)
	private PoliciesEntity policy;

	private Double score;

	public static RecommendationPolicyEntity create(
		RecommendationEntity recommendation,
		PoliciesEntity policy,
		Double score
	) {
		RecommendationPolicyEntity recommendationPolicy = new RecommendationPolicyEntity();
		recommendationPolicy.recommendation = recommendation;
		recommendationPolicy.policy = policy;
		recommendationPolicy.score = score;
		return recommendationPolicy;
	}
}
