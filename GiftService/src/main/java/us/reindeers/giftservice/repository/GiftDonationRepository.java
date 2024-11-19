package us.reindeers.giftservice.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import us.reindeers.giftservice.domain.entity.GiftDonation;
import us.reindeers.giftservice.domain.entity.GiftDonationStatus;

import java.util.List;
import java.util.UUID;

@Repository
public interface GiftDonationRepository extends JpaRepository<GiftDonation, UUID> {

    Page<GiftDonation> findAllByDonorSub(UUID donorSub,Pageable pageable);

    Page<GiftDonation> findAllByReceiverSub(UUID receiverSub,Pageable pageable);

    Page<GiftDonation> findAllByRequestId(UUID requesterSub, Pageable pageable);

    List<GiftDonation> findByRequestId(UUID requestId);

    Page<GiftDonation> findByGiftNameContainingAndReceiverSub(String keyword, UUID receiverSub,Pageable pageable);

    Page<GiftDonation> findByGiftNameContainingAndDonorSub(String keyword, UUID donorSub,Pageable pageable);

    Page<GiftDonation> findByReceiverSub(
            UUID receiverSub,
            Pageable pageable
    );

    Page<GiftDonation> findByReceiverSubAndGiftNameContaining(
            UUID receiverSub,
            String keyword,
            Pageable pageable
    );

    Page<GiftDonation> findByReceiverSubAndDonationStatus(
            UUID receiverSub,
            GiftDonationStatus status,
            Pageable pageable
    );

    Page<GiftDonation> findByReceiverSubAndGiftNameContainingAndDonationStatus(
            UUID receiverSub,
            String keyword,
            GiftDonationStatus status,
            Pageable pageable
    );

    Page<GiftDonation> findByDonorSub(
            UUID donorSub,
            Pageable pageable
    );

    Page<GiftDonation> findByDonorSubAndGiftNameContaining(
            UUID donorSub,
            String keyword,
            Pageable pageable
    );

    Page<GiftDonation> findByDonorSubAndDonationStatus(
            UUID donorSub,
            GiftDonationStatus status,
            Pageable pageable
    );

    Page<GiftDonation> findByDonorSubAndGiftNameContainingAndDonationStatus(
            UUID donorSub,
            String keyword,
            GiftDonationStatus status,
            Pageable pageable
    );
}
