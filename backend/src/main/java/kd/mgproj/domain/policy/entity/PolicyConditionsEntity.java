package kd.mgproj.domain.policy.entity;

import jakarta.persistence.*;
import kd.mgproj.common.entity.BaseEntity;
import kd.mgproj.domain.policy.dto.YouthPolicyItem;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "policy_conditions")
public class PolicyConditionsEntity extends BaseEntity {

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "policy_id", nullable = false, unique = true)
  private PoliciesEntity policy;

  private Integer minAge;

  private Integer maxAge;

  @Column(columnDefinition = "TEXT")
  private String region;

  @Column(columnDefinition = "TEXT")
  private String incomeCondition;

  @Column(columnDefinition = "TEXT")
  private String jobCondition;

  @Column(columnDefinition = "TEXT")
  private String educationCondition;

  @Column(columnDefinition = "TEXT")
  private String additionalCondition;

  @Column(columnDefinition = "TEXT")
  private String excludedTarget;

  public static PolicyConditionsEntity from(
          PoliciesEntity policy,
          YouthPolicyItem item
  ) {
    PolicyConditionsEntity condition = new PolicyConditionsEntity();

    condition.policy = policy;
    condition.minAge = parseInt(item.sprtTrgtMinAge());
    condition.maxAge = parseInt(item.sprtTrgtMaxAge());
    condition.region = item.zipCd();
    condition.incomeCondition = item.earnEtcCn();
    condition.jobCondition = item.jobCd();
    condition.educationCondition = item.schoolCd();
    condition.additionalCondition = item.addAplyQlfcCndCn();
    condition.excludedTarget = item.ptcpPrpTrgtCn();

    return condition;
  }

  public void updateFrom(YouthPolicyItem item) {
    this.minAge = parseInt(item.sprtTrgtMinAge());
    this.maxAge = parseInt(item.sprtTrgtMaxAge());
    this.region = item.zipCd();
    this.incomeCondition = item.earnEtcCn();
    this.jobCondition = item.jobCd();
    this.educationCondition = item.schoolCd();
    this.additionalCondition = item.addAplyQlfcCndCn();
    this.excludedTarget = item.ptcpPrpTrgtCn();
  }

  private static Integer parseInt(String value) {
    if (value == null || value.isBlank()) {
      return null;
    }

    try {
      return Integer.parseInt(value.trim());
    } catch (NumberFormatException e) {
      return null;
    }
  }
}