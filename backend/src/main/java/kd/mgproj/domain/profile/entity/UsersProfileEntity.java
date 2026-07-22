package kd.mgproj.domain.profile.entity;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import kd.mgproj.common.entity.BaseEntity;
import kd.mgproj.common.enums.Gender;
import kd.mgproj.common.enums.HouseholdType;
import kd.mgproj.common.enums.IncomeLevel;
import kd.mgproj.common.enums.JobStatus;
import kd.mgproj.domain.user.entity.UsersEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user_profile")
public class UsersProfileEntity extends BaseEntity {
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private UsersEntity users;

	private LocalDate birthDate;

	private Gender gender;

	private String region;

	private JobStatus jobStatus;

	private IncomeLevel incomeLevel;

	private HouseholdType householdType;

	private String interests;

	public static UsersProfileEntity createUser(
		UsersEntity user,
		LocalDate birthDate,
		Gender gender,
		String region,
		JobStatus jobStatus,
		IncomeLevel incomeLevel,
		HouseholdType householdType,
		String interests
	) {
		return UsersProfileEntity.builder()
			.users(user)
			.birthDate(birthDate)
			.gender(gender)
			.region(region)
			.jobStatus(jobStatus)
			.incomeLevel(incomeLevel)
			.householdType(householdType)
			.interests(interests)
			.build();
	}
}
