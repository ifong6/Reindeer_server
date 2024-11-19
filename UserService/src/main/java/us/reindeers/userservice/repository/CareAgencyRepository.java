package us.reindeers.userservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import us.reindeers.userservice.domain.entity.CareAgency;

import java.util.UUID;

public interface CareAgencyRepository extends JpaRepository<CareAgency, UUID> {

    CareAgency findBySub(UUID sub);
}