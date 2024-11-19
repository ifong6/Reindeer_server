package us.reindeers.giftservice.repository;

import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import us.reindeers.giftservice.domain.entity.GiftRequest;
import us.reindeers.giftservice.domain.entity.GiftRequestStatus;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface GiftRequestRepository extends JpaRepository<GiftRequest, UUID>, JpaSpecificationExecutor<GiftRequest> {

    // GiftRequestRepository.java
    @Query("SELECT gr FROM GiftRequest gr WHERE gr.status <> us.reindeers.giftservice.domain.entity.GiftRequestStatus.CANCELLED AND gr.quantity - gr.inProgressQuantity - gr.receivedQuantity > 0")
    Page<GiftRequest> findActiveGiftRequests(Pageable pageable);

    Page<GiftRequest> findByReceiverSubAndGiftNameContaining(UUID receiverSub, String keyword, Pageable pageable);

    Optional<GiftRequest> findByRequestId(UUID requestId);

    Page<GiftRequest> findByReceiverSub(UUID receiverSub, Pageable pageable);

    Page<GiftRequest> findByReceiverSubAndStatus(
            UUID receiverSub,
            GiftRequestStatus status,
            Pageable pageable
    );

    Page<GiftRequest> findByReceiverSubAndGiftNameContainingAndStatus(
            UUID receiverSub,
            String keyword,
            GiftRequestStatus status,
            Pageable pageable
    );

     // Custom method to find by requestId with a pessimistic lock
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT gr FROM GiftRequest gr WHERE gr.requestId = :requestId")
    Optional<GiftRequest> findByRequestIdForUpdate(UUID requestId);


}
