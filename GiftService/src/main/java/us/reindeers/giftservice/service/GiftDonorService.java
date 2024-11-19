package us.reindeers.giftservice.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import us.reindeers.giftservice.domain.dto.GiftDonationDashboardDto;
import us.reindeers.giftservice.domain.dto.GiftDonationSummaryDto;
import us.reindeers.giftservice.domain.dto.ModifyGiftDonationDto;
import us.reindeers.giftservice.domain.entity.GiftDonation;
import us.reindeers.giftservice.domain.entity.GiftDonationStatus;

import java.util.List;
import java.util.UUID;

public interface GiftDonorService {

    void createDonation(ModifyGiftDonationDto modifyGiftDonationDto, UUID requestId, UUID donorSub);

    void cancelDonation(UUID donationId, UUID donorSub);

    void editDonation(UUID donationId, UUID donorSub, ModifyGiftDonationDto modifyGiftDonationDto);

    Page<GiftDonationDashboardDto> getDashboardDonations(
            UUID donorSub,
            Pageable pageable,
            String keyword,
            GiftDonationStatus status
    );

    GiftDonation getSpecificDonationDonor(UUID donationId, UUID donorSub);

    void updateTrackingNumber(UUID donationId, ModifyGiftDonationDto modifyGiftDonationDto, UUID donorSub);

}
