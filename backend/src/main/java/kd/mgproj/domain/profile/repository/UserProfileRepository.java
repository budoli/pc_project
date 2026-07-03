package kd.mgproj.domain.profile.repository;

import kd.mgproj.domain.profile.entity.UsersProfileEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserProfileRepository extends JpaRepository<UsersProfileEntity, Long> {
}
