package kd.mgproj.domain.user.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import kd.mgproj.common.entity.BaseEntity;
import kd.mgproj.common.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Table(name = "users")
public class UsersEntity extends BaseEntity {
  private String name;

  private String email;

  private String password;

  @Enumerated(EnumType.STRING)
  private Role role;

  public static UsersEntity createUser(
          String name,
          String email,
          String password
  ) {
    return UsersEntity.builder()
            .name(name)
            .email(email)
            .password(password)
            .role(Role.USER)
            .build();
  }
}
