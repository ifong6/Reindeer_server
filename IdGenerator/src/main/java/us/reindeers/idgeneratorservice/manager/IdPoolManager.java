package us.reindeers.idgeneratorservice.manager;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import us.reindeers.common.constant.template.ReturnCode;
import us.reindeers.common.exception.BaseException;
import us.reindeers.idgeneratorservice.repository.IdPoolRepository;
import us.reindeers.idgeneratorservice.service.IdGeneratorService;

@Component
@AllArgsConstructor
public class IdPoolManager {
    private static final Logger log = LoggerFactory.getLogger(IdPoolManager.class);
    private static final int MIN_THRESHOLD = 20;
    private static final int MAX_CAPACITY = 50;

    private final IdPoolRepository idPoolRepository;

    private final IdGeneratorService idGeneratorService;

    @Transactional
    public void autoRefill() {
        try {
            long count = idPoolRepository.count();
            log.info("Current ID count: {}", count);
            if (count < MIN_THRESHOLD) {
                int idsToGenerate = MAX_CAPACITY - (int) count;
                log.info("Refilling ID pool. IDs needed: {}", idsToGenerate);
                fill(idsToGenerate);
                log.info("ID pool refilled successfully.");
            } else {
                log.info("ID pool refill not required.");
            }
        } catch (Exception ex) {
            log.error("Failed to refill ID pool: {}", ex.getMessage());
        }
    }

    @Transactional
    public void manuallyRefill() {
        try {
            fill(20);
            log.info("Added successfully.");
        } catch (Exception ex) {
            log.error("Manual refill failed: {}", ex.getMessage());
            throw new BaseException(ReturnCode.RC500, "Error during manual refill");
        }
    }

    @Transactional
    public void fill(Integer num) {
        try {
            for (int i = 0; i < num; i++) {
                String newId = idGeneratorService.generateUniqueId();
                idGeneratorService.addIdToPool(newId);
                log.debug("Added new ID to pool: {}", newId);
            }
        } catch (Exception ex) {
            log.error("Error during ID pool fill: {}", ex.getMessage());
            throw new BaseException(ReturnCode.RC500, ex.getMessage());
        }
    }
}
