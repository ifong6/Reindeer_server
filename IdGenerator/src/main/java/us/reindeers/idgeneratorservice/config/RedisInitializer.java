package us.reindeers.idgeneratorservice.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import us.reindeers.idgeneratorservice.domain.entity.IdPool;
import us.reindeers.idgeneratorservice.domain.entity.User;
import us.reindeers.idgeneratorservice.repository.IdPoolRepository;
import us.reindeers.idgeneratorservice.repository.UserRepository;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Component
public class RedisInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(RedisInitializer.class);

    @Autowired
    private IdPoolRepository idPoolRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public void run(String... args) {
        // Fetch all IDs from idPool and user tables
        Set<String> idPoolIds = StreamSupport.stream(idPoolRepository.findAll().spliterator(), false)
                .map(IdPool::getId)
                .collect(Collectors.toSet());
        Set<String> userIds = StreamSupport.stream(userRepository.findAll().spliterator(), false)
                .map(User::getUserId)
                .collect(Collectors.toSet());

        // Concatenate both sets
        Set<String> allIds = Stream.concat(idPoolIds.stream(), userIds.stream()).collect(Collectors.toSet());

        // Check if the set is not empty before adding to Redis
        if (!allIds.isEmpty()) {
            redisTemplate.opsForSet().add("ids", allIds.toArray(new String[0]));
            log.info("Initialized Redis with existing IDs from database.");
        } else {
            redisTemplate.opsForSet().add("ids", "init");
            redisTemplate.opsForSet().remove("ids", "init");
            log.warn("No IDs found in database to initialize Redis.");
        }
    }
}
