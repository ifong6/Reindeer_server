package us.reindeers.giftservice.service;

import us.reindeers.common.enums.MessageType;

import java.util.Map;
import java.util.UUID;

public interface SendMessageService {
    void sendNotification(MessageType messageType, UUID receiverSub, Map<String, Object> parameters, String routingKey);
}
