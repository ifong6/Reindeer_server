package us.reindeers.giftservice.domain.model;

import lombok.Builder;
import lombok.Data;
import software.amazon.awssdk.annotations.NotNull;

import java.util.UUID;

@Data
@Builder
public class SubReview {
    @NotNull
    private UUID subReviewId;
    @NotNull
    private String content;
    @NotNull
    private UUID authorSub;
    @NotNull
    private String timestamp;
    private String subReviewImageUrl;
}
