package us.reindeers.userservice.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class AvatarUrlDto {
    @JsonProperty("avatarUrl")
    private String avatarUrl;
}
