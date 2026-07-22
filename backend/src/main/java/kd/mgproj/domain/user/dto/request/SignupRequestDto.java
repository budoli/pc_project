package kd.mgproj.domain.user.dto.request;

import java.time.LocalDate;

import kd.mgproj.common.enums.Gender;
import kd.mgproj.common.enums.HouseholdType;
import kd.mgproj.common.enums.IncomeLevel;
import kd.mgproj.common.enums.JobStatus;

public record SignupRequestDto(
	String email,
	String password,
	String name,
	LocalDate birthDate,
	Gender gender,
	String region,
	JobStatus jobStatus,
	IncomeLevel incomeLevel,
	HouseholdType householdType,
	String interests
) {
}


