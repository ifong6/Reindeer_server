package us.reindeers.giftservice.domain.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import jakarta.persistence.Column;
import lombok.Builder;
import lombok.Data;
import us.reindeers.giftservice.domain.entity.GiftDonationStatus;

@Data
@Builder
public class GiftDonationDetailDto {
  private UUID donationId;
  private UUID requestId;
  private Map donor;
  private Map receiver;
  private String giftName;
  private List<String> images; // gift images retrieved from GiftRequest
  private Integer quantityDonated;
  private GiftDonationStatus donationStatus;
  private String trackingNumber;
  private LocalDateTime donationCreated;
  private LocalDateTime donationUpdated;
  private Boolean isConfirmed;
  private LocalDateTime confirmationTime;
}
