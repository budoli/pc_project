package kd.mgproj.domain.recommendation.entity;

import jakarta.persistence.*;
import kd.mgproj.common.entity.BaseEntity;
import kd.mgproj.domain.policy.entity.PoliciesEntity;
import kd.mgproj.domain.user.entity.UsersEntity;

import java.text.DecimalFormat;

@Entity
@Table(name = "recommendationResult")
public class RecommendationResultEntity extends BaseEntity {
  @ManyToOne(fetch = FetchType.LAZY)
  private UsersEntity users;

  @ManyToOne(fetch = FetchType.LAZY)
  private PoliciesEntity policies;

  private DecimalFormat score;

  @Column(columnDefinition = "TEXT")
  private String reason;
}
