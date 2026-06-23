package kd.mgproj.domain.user.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import kd.mgproj.common.entity.BaseEntity;
import kd.mgproj.common.enums.Role;

@Entity
@Table(name = "users")
public class UsersEntity extends BaseEntity {
  private String name;

  private String email;

  private String password;

  private Role role;
}
