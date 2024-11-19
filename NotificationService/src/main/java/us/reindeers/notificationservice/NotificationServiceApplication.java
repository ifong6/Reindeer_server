package us.reindeers.notificationservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"us.reindeers.notificationservice", "us.reindeers.common"})
public class NotificationServiceApplication {

    public static void main(String[] args){
        SpringApplication.run(NotificationServiceApplication.class, args);
    }
}
