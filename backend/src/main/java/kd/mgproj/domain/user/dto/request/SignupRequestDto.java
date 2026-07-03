package kd.mgproj.domain.user.dto.request;

import kd.mgproj.common.enums.*;
import kd.mgproj.domain.user.entity.UsersEntity;

import java.time.LocalDate;
import java.util.Date;

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
) {}


