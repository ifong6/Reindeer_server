package us.reindeers.common.dto;

import lombok.Builder;
import lombok.Data;
import us.reindeers.common.enums.MessageType;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
public class NotificationMessage {
    private UUID receiverSub;
    private MessageType messageType;
    private LocalDateTime notificationCreated;
    private Map<String, Object> parameters;
}
