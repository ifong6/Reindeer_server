package us.reindeers.idgeneratorservice.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import us.reindeers.idgeneratorservice.manager.IdPoolManager;

@Configuration
@EnableScheduling
public class SchedulerConfig {

    @Autowired
    IdPoolManager idPoolManager;

    @Scheduled(fixedDelay = 300000) // 5 minutes
    public void scheduleFixedRateTask() {
        idPoolManager.autoRefill();
    }
}
