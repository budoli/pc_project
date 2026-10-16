package kd.mgproj.core.security;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import kd.mgproj.common.enums.Role;
import kd.mgproj.domain.user.entity.UsersEntity;

class UserDetailsImplTest {
	@Test
	void mapsAdminRoleToSpringSecurityAuthority() {
		UsersEntity admin = UsersEntity.builder().role(Role.ADMIN).build();

		assertEquals("ROLE_ADMIN", new UserDetailsImpl(admin).getAuthorities().iterator().next().getAuthority());
	}
}
