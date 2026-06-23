package kd.mgproj.domain.profile.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import kd.mgproj.common.entity.BaseEntity;
import kd.mgproj.common.enums.Gender;
import kd.mgproj.common.enums.HouseholdType;
import kd.mgproj.common.enums.IncomeLevel;
import kd.mgproj.common.enums.JobStatus;
import kd.mgproj.domain.user.entity.UsersEntity;

import java.util.Date;

@Entity
@Table(name = "user_profile")
public class UsersProfileEntity extends BaseEntity {
  private UsersEntity users;

  private Date birthDate;

  private Gender gender;

  private String region;

  private JobStatus jobStatus;

  private IncomeLevel incomeLevel;

  private HouseholdType householdType;

  private String interests;
}
