package kd.mgproj.domain.policy.entity;

import jakarta.persistence.*;
import kd.mgproj.common.entity.BaseEntity;
import kd.mgproj.common.enums.*;

@Entity
@Table(name = "policy_conditions")
public class PolicyConditionsEntity extends BaseEntity {
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "policy_id", nullable = false)
  private PoliciesEntity policy;

  private int minAge;

  private int maxAge;

  private Gender gender;

  private String region;

  private IncomeLevel incomeLevel;

  private JobStatus jobStatus;

  private HouseholdType householdType;

  private EducationLevel educationLevel;
}