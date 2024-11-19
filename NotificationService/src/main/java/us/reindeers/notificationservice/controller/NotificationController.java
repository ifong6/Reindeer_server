package us.reindeers.notificationservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import us.reindeers.notificationservice.domain.dto.NotificationDto;
import us.reindeers.notificationservice.service.NotificationService;

import java.util.UUID;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    /**
     * 获取当前用户的通知列表
     * 需要认证
     */
    @GetMapping
    public Page<NotificationDto> getUserNotifications(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        UUID jwtSub = UUID.fromString(jwt.getClaimAsString("sub"));
        return notificationService.getUserNotifications(jwtSub, page, size);
    }
    /**
     * 获取通知的详细内容
     * 需要认证
     */
    @GetMapping("/{notificationId}")
    public NotificationDto getNotificationDetail(@PathVariable UUID notificationId,
                                                 @AuthenticationPrincipal Jwt jwt) {
        UUID jwtSub = UUID.fromString(jwt.getClaimAsString("sub"));
        return notificationService.getNotificationDetail(notificationId, jwtSub);
    }

}
