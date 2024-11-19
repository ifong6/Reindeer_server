package us.reindeers.giftservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import us.reindeers.common.dto.NotificationMessage;
import us.reindeers.common.enums.MessageType;
import us.reindeers.giftservice.service.SendMessageService;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SendMessageServiceImpl implements SendMessageService {

    private final MessageSender messageSender;

    @Override
    public void sendNotification(MessageType messageType, UUID receiverSub, Map<String, Object> parameters, String routingKey) {
        NotificationMessage notificationMessage = NotificationMessage.builder()
                .messageType(messageType)
                .receiverSub(receiverSub)
                .notificationCreated(LocalDateTime.now())
                .parameters(parameters)
                .build();

        messageSender.sendMessage(routingKey, notificationMessage);
    }
}
