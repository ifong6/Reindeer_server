package us.reindeers.notificationservice.domain.dto;

import lombok.Builder;
import lombok.Data;
import us.reindeers.common.enums.MessageType;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
public class NotificationDto {
    private UUID notificationId;
    private UUID receiverSub;
    private MessageType messageType;
    private Map<String, Object> parameters;
    private LocalDateTime notificationCreated;
    private boolean readStatus;
}