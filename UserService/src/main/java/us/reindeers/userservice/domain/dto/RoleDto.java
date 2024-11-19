package us.reindeers.userservice.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class RoleDto {

    @JsonProperty("role")
    private String role;
}
