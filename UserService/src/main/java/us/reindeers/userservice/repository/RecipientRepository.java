package us.reindeers.userservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import us.reindeers.userservice.domain.entity.Recipient;

import java.util.UUID;

public interface RecipientRepository extends JpaRepository<Recipient, UUID> {
    Recipient findBySub(UUID sub);
}
