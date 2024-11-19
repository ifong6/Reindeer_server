package us.reindeers.notificationservice.service;

import org.springframework.data.domain.Page;
import us.reindeers.notificationservice.domain.dto.NotificationDto;

import java.util.UUID;

public interface NotificationService {

    Page<NotificationDto> getUserNotifications(UUID jwtSub, int page, int size);

    NotificationDto getNotificationDetail(UUID notificationId, UUID jwtSub);
}
