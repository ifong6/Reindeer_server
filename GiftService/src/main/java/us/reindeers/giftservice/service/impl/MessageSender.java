package us.reindeers.giftservice.service.impl;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import us.reindeers.common.dto.NotificationMessage;

@Service
public class MessageSender {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void sendMessage(String routingKey, NotificationMessage message) {
        rabbitTemplate.convertAndSend("reindeersExchange", routingKey, message);
    }
}
