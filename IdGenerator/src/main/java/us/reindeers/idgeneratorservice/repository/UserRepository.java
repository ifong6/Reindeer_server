package us.reindeers.idgeneratorservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import us.reindeers.idgeneratorservice.domain.entity.User;

public interface UserRepository extends JpaRepository<User, String> {
}
