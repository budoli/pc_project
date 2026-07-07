package kd.mgproj.domain.recommendation.entity;

import jakarta.persistence.*;
import kd.mgproj.common.entity.BaseEntity;
import kd.mgproj.domain.policy.entity.PoliciesEntity;
import kd.mgproj.domain.user.entity.UsersEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.text.DecimalFormat;

@Entity
@Getter @NoArgsConstructor @AllArgsConstructor
@Table(name = "recommendation")
public class RecommendationEntity extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UsersEntity user;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String question;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String answer;

    public static RecommendationEntity create(
            UsersEntity user,
            String question,
            String answer
    ) {
        RecommendationEntity recommendation = new RecommendationEntity();
        recommendation.user = user;
        recommendation.question = question;
        recommendation.answer = answer;
        return recommendation;
    }
}


