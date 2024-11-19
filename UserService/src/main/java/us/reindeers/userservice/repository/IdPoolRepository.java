package us.reindeers.userservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import us.reindeers.userservice.domain.entity.IdPool;

public interface IdPoolRepository extends JpaRepository<IdPool, String> {
    IdPool findFirstByOrderByIdAsc();
}
