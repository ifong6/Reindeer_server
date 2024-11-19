package us.reindeers.notificationservice.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import us.reindeers.common.dto.NotificationMessage;
import us.reindeers.common.enums.MessageType;
import us.reindeers.notificationservice.domain.entity.Notification;
import us.reindeers.notificationservice.repository.NotificationRepository;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationListener {

    private final NotificationRepository notificationRepository;
    private final RestTemplate restTemplate;

    @Value("${user-service.url}")
    private String userServiceUrl;

    @RabbitListener(queues = "notificationQueue")
    public void handleNotificationMessage(NotificationMessage notificationMessage,
                                          @Header(AmqpHeaders.RECEIVED_ROUTING_KEY) String routingKey) {
        try {
            // 提取消息中的数据
            UUID receiverSub = notificationMessage.getReceiverSub();
            MessageType messageType = notificationMessage.getMessageType();
            LocalDateTime notificationCreated = notificationMessage.getNotificationCreated();

            // 通过 receiverSub 获取 username
            String username = fetchUsernameBySub(receiverSub);

            // 构造 parameters
            Map<String, Object> parameters = constructParameters(notificationMessage.getParameters(), messageType, username);

            // 创建 Notification 实体
            Notification notification = Notification.builder()
                    .receiverSub(receiverSub)
                    .readStatus(false)
                    .messageType(messageType)
                    .parameters(parameters)
                    .notificationCreated(notificationCreated)
                    .build();

            // 保存到数据库
            notificationRepository.save(notification);

            log.info("Received notification message: {}", notificationMessage);

        } catch (Exception e) {
            log.error("Error processing notification message: {}", e.getMessage(), e);
            // 根据需要添加重试或告警逻辑
        }
    }

    private Map<String, Object> constructParameters(Map<String, Object> messageParameters, MessageType messageType, String username) {
        // 初始化 parameters
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("username", username);

        if (messageParameters != null) {
            parameters.putAll(messageParameters);
        }

        // 如果 parameters 中包含 donorSub，处理 donorUsername
        addDonorUsernameIfPresent(parameters);

        // 如果 parameters 中包含 receiverSub，处理 receiverUsername
        addReceiverUsernameIfPresent(parameters);

        return parameters;
    }

    private void addDonorUsernameIfPresent(Map<String, Object> parameters) {
        if (parameters.containsKey("donorSub")) {
            String donorSubStr = (String) parameters.get("donorSub");
            UUID donorUserSub = UUID.fromString(donorSubStr);
            String donorUsername = fetchUsernameBySub(donorUserSub);
            parameters.put("donorUsername", donorUsername);
        }
    }

    private void addReceiverUsernameIfPresent(Map<String, Object> parameters) {
        if (parameters.containsKey("receiverSub")) {
            String receiverSubStr = (String) parameters.get("receiverSub");
            UUID receiverUserSub = UUID.fromString(receiverSubStr);
            String receiverUsername = fetchUsernameBySub(receiverUserSub);
            parameters.put("receiverUsername", receiverUsername);
        }
    }

    private String fetchUsernameBySub(UUID userSub) {
        String url = userServiceUrl + "/api/user/user-public-info/" + userSub.toString();
        try {
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<Map<String, Object>>() {}
            );
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Map<String, Object> body = response.getBody();

                // check data
                Map<String, Object> data = (Map<String, Object>) body.get("data");
                if (data != null) {
                    Object usernameObj = data.get("username");
                    if (usernameObj != null) {
                        return usernameObj.toString();
                    } else {
                        log.error("Username not found in 'data' for sub: {}", userSub);
                        return "User";
                    }
                } else {
                    log.error("No 'data' field in response for sub: {}", userSub);
                    return "User";
                }
            } else {
                log.error("Failed to fetch username for sub: {}", userSub);
                return "User";
            }
        } catch (RestClientException e) {
            log.error("Error fetching username for sub: {}. Error: {}", userSub, e.getMessage());
            return "User";
        }
    }

}
