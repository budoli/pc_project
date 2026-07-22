package kd.mgproj.domain.profile.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import kd.mgproj.domain.profile.entity.UsersProfileEntity;

public interface UserProfileRepository extends JpaRepository<UsersProfileEntity, Long> {
	Optional<UsersProfileEntity> findByUsersId(Long userId);
}
