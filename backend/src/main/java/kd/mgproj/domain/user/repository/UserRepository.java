package kd.mgproj.domain.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import kd.mgproj.domain.user.entity.UsersEntity;

public interface UserRepository extends JpaRepository<UsersEntity, Long> {
	boolean existsByEmail(String email);

	Optional<UsersEntity> findByEmail(String email);

}
