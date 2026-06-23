package kd.mgproj.domain.recommendation.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import kd.mgproj.common.entity.BaseEntity;
import kd.mgproj.domain.policy.entity.PoliciesEntity;
import kd.mgproj.domain.user.entity.UsersEntity;
import org.w3c.dom.Text;

import java.text.DecimalFormat;

@Entity
@Table(name = "recommendationResult")
public class RecommendationResultEntity extends BaseEntity {
  private UsersEntity users;

  private PoliciesEntity policies;

  private DecimalFormat score;

  private Text reason;
}
