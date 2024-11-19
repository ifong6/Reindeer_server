package us.reindeers.userservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import us.reindeers.userservice.domain.entity.User;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findBySub(UUID userSub);

    boolean existsByEmail(String email);
}
