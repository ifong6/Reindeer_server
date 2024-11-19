package us.reindeers.giftservice.utils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import us.reindeers.giftservice.domain.entity.GiftDonation;
import us.reindeers.giftservice.domain.entity.GiftRequest;
import us.reindeers.giftservice.repository.GiftDonationRepository;
import us.reindeers.giftservice.repository.GiftRequestRepository;
import us.reindeers.common.response.constant.template.ReturnCode;
import us.reindeers.common.response.exception.BaseException;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class GiftServiceHelper {

    private final GiftRequestRepository giftRequestRepository;
    private final GiftDonationRepository giftDonationRepository;

    public GiftRequest getSpecificGiftRequest(UUID requestId) {
        return giftRequestRepository.findByRequestId(requestId).orElseThrow(() -> {
            log.error("cannot find requestId {}", requestId);
            return BaseException.builder()
                    .returnCode(ReturnCode.REQUEST_NOT_EXIST).build();
        });
    }

    public GiftDonation getSpecificDonationBySub(UUID donationId, UUID sub) {
        GiftDonation giftDonation = giftDonationRepository.findById(donationId).orElseThrow(() -> {
            log.error("cannot find donationId {}", donationId);
            return BaseException.builder()
                    .returnCode(ReturnCode.RC400)
                    .build();
        });

        if (!sub.equals(giftDonation.getReceiverSub()) && !sub.equals(giftDonation.getDonorSub())) {
            log.error("Invalid user access for donationId {}", donationId);
            throw BaseException.builder()
                    .returnCode(ReturnCode.INVALID_USER)
                    .build();
        }

        return giftDonation;
    }
}