package kd.mgproj.domain.profile.repository;

import kd.mgproj.domain.profile.entity.UsersProfileEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserProfileRepository extends JpaRepository<UsersProfileEntity, Long> {
    Optional<UsersProfileEntity> findByUsersId(Long userId);
}
