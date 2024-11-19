package us.reindeers.giftservice.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import us.reindeers.giftservice.domain.dto.*;
import us.reindeers.giftservice.domain.entity.GiftDonationStatus;
import us.reindeers.giftservice.domain.entity.GiftRequestStatus;
import us.reindeers.giftservice.domain.entity.RequestType;
import us.reindeers.giftservice.domain.model.DonationReview;
import us.reindeers.giftservice.domain.model.SubReview;

import java.util.UUID;

public interface GiftReceiverService {
    UUID createRequest(ModifyGiftRequestDto modifyGiftRequestDto, UUID receiverSub, RequestType requestType);

    void cancelRequest(UUID requestId, UUID receiverSub);

    void editRequest(ModifyGiftRequestDto modifyGiftRequestDto, UUID receiverSub, UUID requestId);

    GiftRequestDetailDto getSpecificRequest(UUID requestId, UUID receiverSub);

    void confirmDonation(UUID donationId, UUID receiverSub);

    void cancelDonation(UUID donationId, UUID receiverSub);

    void receiveDonation(UUID donationId, UUID receiverSub);

    Page<GiftDonationSummaryDto> getGiftDonationsForSpecificRequest(UUID requestId, UUID receiverSub, int page, int size);

    Page<GiftRequestDashboardDto> getDashboardRequests(
            UUID receiverSub,
            Pageable pageable,
            String keyword,
            GiftRequestStatus status
    );

    Page<GiftDonationDashboardDto> getDashboardDonations(
            UUID receiverSub,
            Pageable pageable,
            String keyword,
            GiftDonationStatus status
    );

    void createDonationReview(UUID donationId, DonationReview donationReview, UUID reviewerSub);

    DonationReview getDonationReview(UUID donationId);

    void deleteDonationReview(UUID donationId, UUID reviewerSub);

    void createSubDonationReview(UUID donationId, SubReview subReview, UUID reviewerSub);

    SubReview getSubDonationReview(UUID donationId, UUID subReviewId);

    void deleteSubDonationReview(UUID donationId, UUID subReviewId, UUID reviewerSub);

}
