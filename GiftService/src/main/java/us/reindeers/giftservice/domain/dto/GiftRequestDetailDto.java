package us.reindeers.giftservice.domain.dto;

import lombok.Builder;
import lombok.Data;
import us.reindeers.giftservice.domain.entity.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
public class GiftRequestDetailDto {
    private Map receiver;
    private UUID requestId;
    private RequestType requestType;
    private String giftName;
    private Integer quantity;
    private String description;
    private List<String> images;
    private GiftCategory giftCategory;
    private Integer estimatedPrice;
    private ChildInfo childInfo;
    private Integer inProgressQuantity;
    private Integer receivedQuantity;
    private LocalDateTime requestCreated;
    private LocalDateTime requestUpdated;
    private GiftRequestStatus status;

    public GiftRequestDetailDto(GiftRequest giftRequest, Map receiver) {
        this.receiver = receiver;
        this.requestId = giftRequest.getRequestId();
        this.requestType = giftRequest.getRequestType();
        this.giftName = giftRequest.getGiftName();
        this.quantity = giftRequest.getQuantity();
        this.description = giftRequest.getDescription();
        this.images = giftRequest.getImages();
        this.giftCategory = giftRequest.getGiftCategory();
        this.estimatedPrice = giftRequest.getEstimatedPrice();
        this.childInfo = giftRequest.getChildInfo();
        this.inProgressQuantity = giftRequest.getInProgressQuantity();
        this.receivedQuantity = giftRequest.getReceivedQuantity();
        this.requestCreated = giftRequest.getRequestCreated();
        this.requestUpdated = giftRequest.getRequestUpdated();
        this.status = giftRequest.getStatus();
    }
}
