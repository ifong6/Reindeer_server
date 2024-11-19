package us.reindeers.userservice.domain.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class PersonalInfoDto {
    UUID sub;
    String userId;
    String username;
    String email;
    String avatarUrl;
    String role;
    String accountCreated;
    String address;
    String phone;
    String website;
    String description;
    String age;
    String interest;
    String story;
    String introduction;
}
