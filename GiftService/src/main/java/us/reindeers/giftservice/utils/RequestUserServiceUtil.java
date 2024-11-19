package us.reindeers.giftservice.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.UUID;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RequestUserServiceUtil {

  private final RestTemplate restTemplate;

  @Value("${UserService.UserInfoUrl}")
  private String userInfoUrl;

  public Map getUserDetailedInfo(UUID sub) {
    // Retrieve the receiver detail from UserService
    String getReceiverUrl = userInfoUrl + sub;
    ResponseEntity<Map> userServiceResponse = restTemplate.getForEntity(getReceiverUrl, Map.class);
    return userServiceResponse.getBody();
  }
}
