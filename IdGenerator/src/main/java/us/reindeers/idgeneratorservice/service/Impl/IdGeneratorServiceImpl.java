package us.reindeers.idgeneratorservice.service.Impl;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import us.reindeers.common.constant.template.ReturnCode;
import us.reindeers.common.exception.BaseException;
import us.reindeers.idgeneratorservice.domain.dto.CurrentNumberDto;
import us.reindeers.idgeneratorservice.domain.entity.IdPool;
import us.reindeers.idgeneratorservice.repository.IdPoolRepository;
import us.reindeers.idgeneratorservice.service.IdGeneratorService;

import java.security.SecureRandom;
import java.util.Random;

@Service
@AllArgsConstructor
public class IdGeneratorServiceImpl implements IdGeneratorService {
    private static final Logger log = LoggerFactory.getLogger(IdGeneratorService.class);
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int ID_LENGTH = 10;
    private final Random random = new SecureRandom();

    private final IdPoolRepository idPoolRepository;
    private final StringRedisTemplate redisTemplate;

    public void addIdToPool(String id) {
        try {
            IdPool newId = new IdPool();
            newId.setId(id);
            idPoolRepository.save(newId);
            redisTemplate.opsForSet().add("ids", id);
        } catch (DataAccessException ex) {
            log.error("Error saving ID to the database: {}", ex.getMessage());
            throw new BaseException(ReturnCode.RC500, "Unable to save ID");
        }
    }

    public synchronized String generateUniqueId() {
        try {
            String id;
            do {
                id = generateId();
            } while (Boolean.TRUE.equals(redisTemplate.opsForSet().isMember("ids", id)));
            return id;
        } catch (Exception ex) {
            log.error("Error generating unique ID: {}", ex.getMessage());
            throw new BaseException(ReturnCode.RC500, "Error during ID generation");
        }
    }

    public String generateId() {
        StringBuilder builder = new StringBuilder(ID_LENGTH);
        for (int i = 0; i < ID_LENGTH; i++) {
            builder.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }
        return builder.toString();
    }

    public CurrentNumberDto getCurrentNumber() {
        try {
            return CurrentNumberDto.builder()
                    .currentNumber(idPoolRepository.count()).build();
        } catch (DataAccessException ex) {
            log.error("Error accessing database to count IDs: {}", ex.getMessage());
            throw new BaseException(ReturnCode.RC500, "Error retrieving ID count");
        }
    }
}
