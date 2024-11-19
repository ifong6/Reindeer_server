package us.reindeers.giftservice.domain.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class DonationNotificationDto {
    UUID ownerSub;
    UUID orderId;
    String message;
}
