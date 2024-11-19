package us.reindeers.giftservice.domain.dto;

import java.util.List;
import java.util.UUID;

import lombok.Builder;
import lombok.Data;
import us.reindeers.giftservice.domain.entity.GiftCategory;
import us.reindeers.giftservice.domain.entity.GiftRequest;
import us.reindeers.giftservice.domain.entity.GiftRequestStatus;
import us.reindeers.giftservice.domain.entity.RequestType;

/**
 * Basic Info of GiftRequest which is suitable to return in lists
 */
@Data
@Builder
public class GiftRequestSummaryDto {

  private String receiverUsername;
  private UUID requestId;
  private RequestType requestType;
  private String giftName;
  private Integer neededQuantity;
  private List<String> images;
  private GiftCategory giftCategory;
  // not the original estimatedPrice, but the price calculated based on neededQuantity
  private Integer estimatedPrice;
  private GiftRequestStatus status;

}
