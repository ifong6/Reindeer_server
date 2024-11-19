package us.reindeers.giftservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"us.reindeers.giftservice", "us.reindeers.common"})
public class GiftServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(GiftServiceApplication.class, args);
    }
}
