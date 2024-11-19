package us.reindeers.giftservice.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import us.reindeers.giftservice.utils.ChildInfoConverter;
import us.reindeers.giftservice.utils.JsonToListConverter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Entity
@Table(name = "gift_request")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GiftRequest {
    @Id
    @Column(name = "request_id", columnDefinition = "BINARY(16)", updatable = false, nullable = false)
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID requestId;

    @Column(name = "receiver_sub", columnDefinition = "BINARY(16)", updatable = false, nullable = false)
    private UUID receiverSub;

    @Enumerated(EnumType.STRING)
    @Column(name = "request_type")
    private RequestType requestType;

    @Enumerated(EnumType.STRING)
    @Column(name = "gift_category")
    private GiftCategory giftCategory;

    @Column(name = "gift_name", columnDefinition = "varchar(100)")
    private String giftName;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "images", columnDefinition = "JSON")
    @Convert(converter = JsonToListConverter.class)
    private List<String> images;

    @Column(name = "estimated_price")
    private Integer estimatedPrice;

    @Column(name = "child_info", columnDefinition = "JSON")
    @Convert(converter = ChildInfoConverter.class)
    private ChildInfo childInfo;

    @Column(name = "in_progress_quantity")
    private Integer inProgressQuantity;

    @Column(name = "received_quantity")
    private Integer receivedQuantity;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private GiftRequestStatus status;

    @Column(name = "request_created")
    private LocalDateTime requestCreated;

    @Column(name = "request_updated")
    private LocalDateTime requestUpdated;

    @PreUpdate
    protected void onUpdate(){
        this.requestUpdated = LocalDateTime.now();
    }

    @PrePersist
    protected void onCreate(){
        this.requestCreated = LocalDateTime.now();
        this.requestUpdated = LocalDateTime.now();
    }
}

