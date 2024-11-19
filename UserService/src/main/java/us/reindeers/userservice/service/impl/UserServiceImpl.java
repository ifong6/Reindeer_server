/*
*
* 暂时将这个用了mq的add User方法注释掉
* */


//package us.reindeers.userservice.service.impl;
//
//import lombok.RequiredArgsConstructor;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.amqp.core.AmqpAdmin;
//import org.springframework.amqp.rabbit.annotation.RabbitListener;
//import org.springframework.amqp.rabbit.core.RabbitTemplate;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.dao.DataAccessException;
//import org.springframework.http.ResponseEntity;
//import org.springframework.scheduling.annotation.Async;
//import org.springframework.scheduling.annotation.EnableAsync;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Isolation;
//import org.springframework.transaction.annotation.Transactional;
//import org.springframework.web.client.RestTemplate;
//import us.reindeers.common.exception.BaseException;
//import us.reindeers.userservice.domain.dto.UserDto;
//import us.reindeers.userservice.domain.entity.IdPool;
//import us.reindeers.userservice.domain.entity.RoleEnum;
//import us.reindeers.userservice.domain.entity.User;
//import us.reindeers.userservice.repository.IdPoolRepository;
//import us.reindeers.userservice.repository.UserRepository;
//import us.reindeers.userservice.service.UserService;
//
//import java.time.LocalDateTime;
//
//import static us.reindeers.common.constant.template.ReturnCode.*;
//
//@Service
//@EnableAsync
//@RequiredArgsConstructor
//public class UserServiceImpl implements UserService {
//
//    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);
//    private final UserRepository userRepository;
//    private final IdPoolRepository idPoolRepository;
//    private final RestTemplate restTemplate;
//    private final RabbitTemplate rabbitTemplate;
//    private final AmqpAdmin amqpAdmin;
//
//    @Value("${rabbitmq.queue}")
//    private String refillQueueName;
//
//    @Value("${rabbitmq.exchange}")
//    private String exchangeName;
//
//    @Value("${rabbitmq.routingKey}")
//    private String routingKey;
//
//    @Override
//    @Transactional(isolation = Isolation.READ_COMMITTED) // Roll back on error
//    public void addUser(UserDto userDto) {
//        try {
//            IdPool idPool = obtainIdPool();
//            if (idPool == null) {
//                throw BaseException.builder()
//                        .returnCode(ID_OBTAINED_FAILURE).build();
//            }
//            //delete this id from the pool
//            idPoolRepository.delete(idPool);
//
//            User user = User.builder()
//                    .sub(userDto.getSub())
//                    .username(userDto.getUsername())
//                    .email(userDto.getEmail())
//                    .userId(idPool.getId())
//                    .avatarUrl("default-s3-url")
//                    .role(RoleEnum.DEFAULT)
//                    .accountCreated(LocalDateTime.now())
//                    .accountUpdated(LocalDateTime.now()).build();
//
//            //save the user
//            userRepository.save(user);
//
//            //send welcome message
//            // TODO
//
//
//        } catch (DataAccessException e) {
//            throw BaseException.builder()
//                    .returnCode(DATABASE_ACCESS_ERROR)
//                    .message(e.getMessage())
//                    .build();
//        } catch (IllegalArgumentException e) {
//            throw BaseException.builder()
//                    .returnCode(INVALID_INPUT)
//                    .message(e.getMessage())
//                    .build();
//        } catch (Exception e) {
//            throw BaseException.builder()
//                    .returnCode(RC500)
//                    .message(e.getMessage())
//                    .build();
//        }
//    }
//
//    // fetch an id from the pool
//    private IdPool obtainIdPool() {
//        IdPool idPool = null;
//        int retryCount = 0;
//        while (idPool == null && retryCount < 5) {
//            // try to fetch an id
//            idPool = idPoolRepository.findFirstByOrderByIdAsc();
//            if (idPool == null) {
//                log.warn("IdPool is null, starting refill task at retry {}", retryCount);
//                //use rabbitmq to refill id_pool manually
//                publishToRefillQueue();
//            }
//            retryCount++;
//            try {
//                Thread.sleep(1000);
//            } catch (InterruptedException e) {
//                Thread.currentThread().interrupt();
//                log.error("Thread was interrupted", e);
//            }
//        }
//        if (idPool == null) {
//            log.error("Failed to obtain an ID after 5 retries");
//        }
//        return idPool;
//    }
//
//    @Async
//    public void publishToRefillQueue() {
//        log.info("Publishing message to RabbitMQ to refill ID pool");
//        rabbitTemplate.convertAndSend(exchangeName, routingKey, "Refill ID Pool");
//    }
//
//    @RabbitListener(queues = "${rabbitmq.queue}")
//    public void handleRefillTask(String message) {
//        log.info("Received message to refill ID pool: {}", message);
//        refillIdPool();
//    }
//
//    // use api from idGenerator to refill id_pool manually
//    public void refillIdPool() {
//        log.info("Attempting to refill ID pool");
//        //change the url
//        ResponseEntity<String> refillResponse = restTemplate.getForEntity(
//                "http://localhost:8081/id/refill", String.class
//        );
//        log.info("Refill response status: {}", refillResponse.getStatusCode());
//        if (!refillResponse.getStatusCode().is2xxSuccessful()) {
//            log.warn("Failed to refill ID pool");
//        }
//    }
//}