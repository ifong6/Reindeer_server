package us.reindeers.userservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import us.reindeers.userservice.domain.entity.Donor;

import java.util.UUID;

public interface DonorRepository extends JpaRepository<Donor, UUID> {
    Donor findBySub(UUID sub);
}
