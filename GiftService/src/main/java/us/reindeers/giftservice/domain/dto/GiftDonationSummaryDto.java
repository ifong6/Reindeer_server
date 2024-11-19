package us.reindeers.giftservice.domain.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import lombok.Builder;
import lombok.Data;
import us.reindeers.giftservice.domain.entity.GiftCategory;
import us.reindeers.giftservice.domain.entity.GiftDonationStatus;
import us.reindeers.giftservice.domain.entity.GiftRequest;
import us.reindeers.giftservice.domain.entity.RequestType;

/**
 * Basic Info of GiftDonation which is suitable to return in lists
 */
@Data
@Builder
public class GiftDonationSummaryDto {
  private UUID donationId;
  private UUID requestId;
  private String giftName;
  private List<String> images; // gift images retrieved from GiftRequest
  private Integer quantityDonated;
  private String donorUsername;
  private String receiverUsername;
  private GiftCategory giftCategory;
  private UUID donorSub;
  private UUID receiverSub;
  private GiftDonationStatus donationStatus;
  private String trackingNumber;
  private LocalDateTime donationCreated;
  private LocalDateTime donationUpdated;
  private Boolean isConfirmed;
  private LocalDateTime confirmationTime;
  private String address;
}
