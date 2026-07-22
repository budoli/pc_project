package kd.mgproj.domain.user.dto.response;

import kd.mgproj.common.enums.Role;
import kd.mgproj.domain.user.entity.UsersEntity;

public record UserResponseDto(
	long id,
	String name,
	Role role
) {
	public static UserResponseDto from(UsersEntity user) {
		return new UserResponseDto(
			user.getId(),
			user.getName(),
			user.getRole()
		);
	}
}


