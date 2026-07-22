package kd.mgproj.domain.conversation.dto.request;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

import kd.mgproj.common.enums.RegionCode;
import kd.mgproj.domain.profile.entity.UsersProfileEntity;

public record AiConversationRequest(
	String query,
	UserProfilePayload profile,
	List<AiMessage> messages
) {
	public record UserProfilePayload(
		Integer age,
		String gender,
		String region,
		String regionCode,
		String jobStatus,
		String incomeLevel,
		String householdType,
		String interests
	) {
		public static UserProfilePayload from(UsersProfileEntity profile) {
			if (profile == null) {
				return null;
			}

			return new UserProfilePayload(
				calculateAge(profile.getBirthDate()),
				profile.getGender() == null ? null : profile.getGender().name(),
				RegionCode.labelOf(RegionCode.codeOf(profile.getRegion())),
				RegionCode.codeOf(profile.getRegion()),
				profile.getJobStatus() == null ? null : profile.getJobStatus().name(),
				profile.getIncomeLevel() == null ? null : profile.getIncomeLevel().name(),
				profile.getHouseholdType() == null ? null : profile.getHouseholdType().name(),
				profile.getInterests()
			);
		}

		private static Integer calculateAge(LocalDate birthDate) {
			if (birthDate == null) {
				return null;
			}

			return Period.between(birthDate, LocalDate.now()).getYears();
		}
	}

	public record AiMessage(
		String role,
		String content
	) {
	}
}
