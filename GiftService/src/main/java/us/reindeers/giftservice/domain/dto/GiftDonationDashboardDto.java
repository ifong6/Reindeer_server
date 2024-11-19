package us.reindeers.giftservice.domain.dto;

import lombok.Builder;
import lombok.Data;
import us.reindeers.giftservice.domain.entity.GiftDonationStatus;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class GiftDonationDashboardDto {
    private UUID donationId;
    private String giftName;
    private GiftDonationStatus donationStatus;
    private Integer quantityDonated;
    private UUID donorSub;
    private UUID receiverSub;
    private LocalDateTime donationCreated;
    private LocalDateTime donationUpdated;
}