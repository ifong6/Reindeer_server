package us.reindeers.notificationservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import us.reindeers.notificationservice.domain.dto.NotificationDto;
import us.reindeers.notificationservice.domain.entity.Notification;
import us.reindeers.notificationservice.repository.NotificationRepository;
import us.reindeers.notificationservice.service.NotificationService;
import us.reindeers.common.response.constant.template.ReturnCode;
import us.reindeers.common.response.exception.BaseException;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;

    @Override
    public Page<NotificationDto> getUserNotifications(UUID jwtSub, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "notificationCreated"));

        Page<Notification> notifications = notificationRepository.findByReceiverSub(jwtSub, pageable);

        return notifications.map(this::convertToDto);  // Page already handles streaming
    }


    @Override
    public NotificationDto getNotificationDetail(UUID notificationId, UUID jwtSub) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new BaseException(ReturnCode.NOTIFICATION_NOT_FOUND));

        // 验证通知是否属于当前用户
        if (!notification.getReceiverSub().equals(jwtSub)) {
            log.error("User {} is not authorized to access notification {}", jwtSub, notificationId);
            throw BaseException.builder()
                    .returnCode(ReturnCode.INVALID_USER)
                    .build();
        }

        // 可选：标记为已读
        if (!notification.isReadStatus()) {
            notification.setReadStatus(true);
            notificationRepository.save(notification);
        }

        return convertToDto(notification);
    }

    private NotificationDto convertToDto(Notification notification) {
        return NotificationDto.builder()
                .notificationId(notification.getNotificationId())
                .receiverSub(notification.getReceiverSub())
                .messageType(notification.getMessageType())
                .parameters(notification.getParameters())
                .notificationCreated(notification.getNotificationCreated())
                .readStatus(notification.isReadStatus())
                .build();
    }
}
