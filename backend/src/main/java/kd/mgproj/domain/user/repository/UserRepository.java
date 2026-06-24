package kd.mgproj.domain.user.repository;

import kd.mgproj.domain.user.entity.UsersEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UsersEntity, Long> {
    boolean existsByEmail(String email);

    Optional<UsersEntity> findByEmail(String email);

}
