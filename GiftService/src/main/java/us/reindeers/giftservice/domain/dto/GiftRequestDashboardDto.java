package us.reindeers.giftservice.domain.dto;

import lombok.Builder;
import lombok.Data;
import us.reindeers.giftservice.domain.entity.GiftRequestStatus;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class GiftRequestDashboardDto {
    private UUID requestId;
    private String giftName;
    private GiftRequestStatus status;

    private Integer neededQuantity;
    private Integer receivedQuantity;
    private Integer inProgressQuantity;

    private LocalDateTime requestCreated;
    private LocalDateTime requestUpdated;
}