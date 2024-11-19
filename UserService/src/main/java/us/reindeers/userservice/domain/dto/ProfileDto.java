package us.reindeers.userservice.domain.dto;

import lombok.Data;

@Data
public class ProfileDto {
    private String username;

    // CareAgency fields
    private String address;
    private String phone;
    private String website;
    private String description;

    // Recipient fields
    private Integer age;
    private String story;
    private String interest;

    // Donor fields
    private String introduction;
}
