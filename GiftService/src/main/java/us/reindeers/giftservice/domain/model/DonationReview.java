package us.reindeers.giftservice.domain.model;

import lombok.Builder;
import lombok.Data;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class DonationReview {
    @NotNull
    private UUID donationId;
    @NotNull
    private UUID reviewerSub;
    @NotNull
    private String reviewContent;
    @NotNull
    private String reviewCreated;
    private String reviewImageUrl;
    private List<SubReview> subReviews;
}
