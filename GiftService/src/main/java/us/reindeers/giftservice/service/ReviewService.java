package us.reindeers.giftservice.service;

import us.reindeers.giftservice.domain.model.DonationReview;
import us.reindeers.giftservice.domain.model.SubReview;

import java.util.UUID;

public interface ReviewService {
    void createReview(DonationReview donationReview);
    void createSubReview(UUID donationId, SubReview subReview);
    DonationReview getReview(UUID donationId);
    SubReview getSubReview(UUID donationId, UUID subReviewId);
    void deleteReview(UUID donationId);
    void deleteSubReview(UUID donationId, UUID subReviewId);
}
