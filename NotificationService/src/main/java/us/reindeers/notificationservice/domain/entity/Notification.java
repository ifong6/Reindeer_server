package us.reindeers.notificationservice.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import us.reindeers.common.enums.MessageType;
import us.reindeers.notificationservice.utils.MapToJsonConverter;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "notification")
public class Notification {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "notification_id", columnDefinition = "BINARY(16)", updatable = false, nullable = false)
    private UUID notificationId;

    @Column(name = "receiver_sub", columnDefinition = "BINARY(16)", updatable = false, nullable = false)
    private UUID receiverSub;

    @Column(name = "read_status", columnDefinition = "bool", nullable = false)
    private boolean readStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "message_type", columnDefinition = "varchar(50)", nullable = false, updatable = false)
    private MessageType messageType;

    @Convert(converter = MapToJsonConverter.class)
    @Column(name = "parameters", columnDefinition = "TEXT")
    private Map<String, Object> parameters;

    @Column(name = "notification_created")
    private LocalDateTime notificationCreated;
}
