package us.reindeers.idgeneratorservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import us.reindeers.idgeneratorservice.domain.entity.IdPool;

public interface IdPoolRepository extends JpaRepository<IdPool, String> {
}
