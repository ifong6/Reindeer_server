package us.reindeers.giftservice.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "gift_donation")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GiftDonation {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "donation_id", columnDefinition = "BINARY(16)", updatable = false, nullable = false)
    private UUID donationId;

    @Column(name = "request_id", columnDefinition = "BINARY(16)", updatable = false, nullable = false)
    private UUID requestId;

    @Column(name = "donor_sub", columnDefinition = "BINARY(16)", updatable = false, nullable = false)
    private UUID donorSub;

    @Column(name = "receiver_sub", columnDefinition = "BINARY(16)", updatable = false, nullable = false)
    private UUID receiverSub;

    @Column(name = "gift_name", columnDefinition = "varchar(100)")
    private String giftName;

    @Column(name = "quantity_donated")
    private Integer quantityDonated;

    @Enumerated(EnumType.STRING)
    @Column(name = "donation_status")
    private GiftDonationStatus donationStatus;

    @Column(name = "tracking_number", length = 100)
    private String trackingNumber;

    @Column(name = "donation_created")
    private LocalDateTime donationCreated;

    @Column(name = "donation_updated")
    private LocalDateTime donationUpdated;

    //donation receiver 同意后转为true
    @Column(name = "is_confirmed")
    private Boolean isConfirmed;

    //receiver 同意时的时间
    @Column(name = "confirmation_time")
    private LocalDateTime confirmationTime;

    @PreUpdate
    protected void onUpdate(){
        this.donationUpdated = LocalDateTime.now();
    }

    @PrePersist
    protected void onCreate(){
        this.donationCreated = LocalDateTime.now();
        this.donationUpdated = LocalDateTime.now();
    }
}

