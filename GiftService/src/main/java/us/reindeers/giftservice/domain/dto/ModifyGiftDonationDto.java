package us.reindeers.giftservice.domain.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

/*
* 用于传递GiftDonation信息
* 用在创建、修改、、取消Donation
* */

@Data
@Builder
public class ModifyGiftDonationDto {
    private UUID donationId;
    private Integer quantityDonated;
    private String giftName;
    private String trackingNumber;
}
