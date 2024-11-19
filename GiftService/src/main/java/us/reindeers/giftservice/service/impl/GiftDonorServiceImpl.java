package us.reindeers.giftservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import us.reindeers.common.enums.MessageType;
import us.reindeers.giftservice.domain.dto.*;
import us.reindeers.giftservice.domain.entity.GiftDonation;
import us.reindeers.giftservice.domain.entity.GiftDonationStatus;
import us.reindeers.giftservice.domain.entity.GiftRequest;
import us.reindeers.giftservice.domain.entity.GiftRequestStatus;
import us.reindeers.giftservice.repository.GiftDonationRepository;
import us.reindeers.giftservice.repository.GiftRequestRepository;
import us.reindeers.giftservice.service.GiftDonorService;
import us.reindeers.giftservice.service.SendMessageService;
import us.reindeers.giftservice.utils.DTOMapper;
import us.reindeers.giftservice.utils.GiftServiceHelper;
import us.reindeers.giftservice.utils.UpdateUtil;
import us.reindeers.common.response.constant.template.ReturnCode;
import us.reindeers.common.response.exception.BaseException;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class GiftDonorServiceImpl implements GiftDonorService {

    private final GiftDonationRepository giftDonationRepository;
    private final GiftRequestRepository giftRequestRepository;
    private final GiftServiceHelper giftServiceHelper;
    private final DTOMapper dtoMapper;
    private final RedissonClient redissonClient;
    private final SendMessageService sendMessageService;

    @Override
    @Transactional
    public void createDonation(ModifyGiftDonationDto modifyGiftDonationDto, UUID requestId, UUID donorSub) {
        // Acquire Redis distributed lock to ensure only one process can operate on the same request at the same time
        RLock lock = redissonClient.getLock("gift_request_lock_" + requestId);

        try {
            // Try to acquire the lock, wait up to 10 seconds, and hold the lock for 60 seconds if acquired
            if (lock.tryLock(10, 60, TimeUnit.SECONDS)) {
                // Find the gift request and apply pessimistic lock in the database (SELECT FOR UPDATE)
                GiftRequest giftRequest = giftRequestRepository.findByRequestIdForUpdate(requestId).orElseThrow(() -> {
                    log.error("Cannot find requestId {}", requestId);
                    return BaseException.builder()
                            .returnCode(ReturnCode.REQUEST_NOT_EXIST)
                            .build();
                });

                // Check if the request is already fulfilled to avoid creating a donation
                if (giftRequest.getStatus().equals(GiftRequestStatus.FULFILLED_IN_PROGRESS)) {
                    log.info("Gift request {} quantity is fulfilled, please check other requests", requestId);
                    throw BaseException.builder()
                            .returnCode(ReturnCode.REQUEST_IS_FULFILLED)
                            .build();
                }

                // Update the in-progress quantity in the gift request and check if it fulfills the request
                Integer newInProgressQuantity = giftRequest.getInProgressQuantity() + modifyGiftDonationDto.getQuantityDonated();
                UpdateUtil.updateIfPresent(newInProgressQuantity, giftRequest::setInProgressQuantity);
                Integer remainingQuantity = giftRequest.getQuantity() - giftRequest.getReceivedQuantity();

                if (newInProgressQuantity.compareTo(remainingQuantity) >= 0) {
                    log.info("New donation fulfills the request.");
                    giftRequest.setStatus(GiftRequestStatus.FULFILLED_IN_PROGRESS);
                } else {
                    giftRequest.setStatus(GiftRequestStatus.IN_PROGRESS);
                }

                giftRequestRepository.save(giftRequest);

                // Create the GiftDonation entity
                GiftDonation giftDonation = GiftDonation.builder()
                        .requestId(requestId)
                        .quantityDonated(modifyGiftDonationDto.getQuantityDonated())
                        .donorSub(donorSub)
                        .receiverSub(giftRequest.getReceiverSub())
                        .giftName(giftRequest.getGiftName())
                        .donationStatus(GiftDonationStatus.PENDING)
                        .isConfirmed(false)
                        .build();

                giftDonationRepository.save(giftDonation);

                // Send notifications to both the receiver and the donor
                // Notification to the receiver
                Map<String, Object> receiverParameters = new HashMap<>();
                receiverParameters.put("requestId", requestId.toString());
                receiverParameters.put("donorSub", donorSub.toString());
                receiverParameters.put("donationId", giftDonation.getDonationId().toString());

                sendMessageService.sendNotification(MessageType.DONATION_RECEIVED,giftRequest.getReceiverSub(),receiverParameters,"notification.donation.created");

                // Notification to the donor
                Map<String, Object> donorParameters = new HashMap<>();
                donorParameters.put("requestId", requestId.toString());
                donorParameters.put("receiverSub", giftRequest.getReceiverSub().toString());
                donorParameters.put("donationId", giftDonation.getDonationId().toString());

                sendMessageService.sendNotification(MessageType.DONATION_RECEIVED,donorSub,donorParameters,"notification.donation.created");

            } else {
                // If the lock could not be acquired, throw an exception or handle it accordingly
                throw new RuntimeException("Failed to acquire lock for requestId: " + requestId);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Interrupted while trying to acquire lock", e);
        } finally {
            // Ensure the Redis lock is always released after the operation
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    @Override
    public void cancelDonation(UUID donationId, UUID donorSub) {
        GiftDonation giftDonation = getSpecificDonationDonor(donationId, donorSub);
        GiftDonationStatus status = giftDonation.getDonationStatus();

        if (!status.equals(GiftDonationStatus.PENDING) &&
                !status.equals(GiftDonationStatus.PROCESSING) &&
                !status.equals(GiftDonationStatus.RECEIVER_CANCELLED)) {

            log.error("Donation can only be cancelled in pending, processing or receiver cancelled status");
            throw BaseException.builder()
                    .returnCode(ReturnCode.INVALID_DONATION_STATUS)
                    .build();
        }

        if (status.equals(GiftDonationStatus.PENDING)) {
            giftDonation.setDonationStatus(GiftDonationStatus.CANCELLED);

            // Inform both sides the donation was cancelled
            notifyCancellation(giftDonation, donorSub, "notification.donation.cancelled");

            resetQuantityAfterCancel(giftDonation, findGiftRequestOrFail(giftDonation.getRequestId()));
        } else if (status.equals(GiftDonationStatus.PROCESSING)) {
            giftDonation.setDonationStatus(GiftDonationStatus.DONOR_CANCELLED);

            // Send notification to both sides that the cancellation needs approval
            notifyCancellationWithApproval(giftDonation, donorSub, "notification.donation.cancelled.needs_approval");
        } else {
            giftDonation.setDonationStatus(GiftDonationStatus.CANCELLED);
            resetQuantityAfterCancel(giftDonation, findGiftRequestOrFail(giftDonation.getRequestId()));

            // Inform both sides the donation was cancelled
            notifyCancellation(giftDonation, donorSub, "notification.donation.cancelled");
        }

        giftDonationRepository.save(giftDonation);
    }

    private GiftRequest findGiftRequestOrFail(UUID requestId) {
        return giftRequestRepository.findByRequestId(requestId).orElseThrow(() -> {
            log.error("Cannot find requestId {}", requestId);
            return BaseException.builder()
                    .returnCode(ReturnCode.REQUEST_NOT_EXIST)
                    .build();
        });
    }

    private void notifyCancellation(GiftDonation giftDonation, UUID donorSub, String routingKey) {
        // Notification to receiver
        Map<String, Object> receiverParameters = new HashMap<>();
        receiverParameters.put("requestId", giftDonation.getRequestId().toString());
        receiverParameters.put("donationId", giftDonation.getDonationId().toString());
        receiverParameters.put("donorSub", donorSub.toString());
        sendMessageService.sendNotification(MessageType.DONATION_CANCELLED, giftDonation.getReceiverSub(), receiverParameters, routingKey);

        // Notification to donor
        Map<String, Object> donorParameters = new HashMap<>();
        donorParameters.put("requestId", giftDonation.getRequestId().toString());
        donorParameters.put("donationId", giftDonation.getDonationId().toString());
        donorParameters.put("receiverSub", giftDonation.getReceiverSub().toString());
        sendMessageService.sendNotification(MessageType.DONATION_CANCELLED, donorSub, donorParameters, routingKey);
    }

    private void notifyCancellationWithApproval(GiftDonation giftDonation, UUID donorSub, String routingKey) {
        // To donor: You have cancelled one donation but needs to be approved
        Map<String, Object> donorParameters = new HashMap<>();
        donorParameters.put("requestId", giftDonation.getRequestId().toString());
        donorParameters.put("donationId", giftDonation.getDonationId().toString());
        donorParameters.put("receiverSub", giftDonation.getReceiverSub().toString());
        sendMessageService.sendNotification(MessageType.DONATION_CANCELLED_NEEDS_APPROVAL, donorSub, donorParameters, routingKey);

        // To receiver: A donor wants to cancel his donation, please approve
        Map<String, Object> receiverParameters = new HashMap<>();
        receiverParameters.put("requestId", giftDonation.getRequestId().toString());
        receiverParameters.put("donationId", giftDonation.getDonationId().toString());
        receiverParameters.put("donorSub", donorSub.toString());
        sendMessageService.sendNotification(MessageType.DONATION_CANCELLED_NEEDS_APPROVAL, giftDonation.getReceiverSub(), receiverParameters, routingKey);
    }

    private void resetQuantityAfterCancel(GiftDonation giftDonation, GiftRequest giftRequest) {
        int restoredQuantity = giftRequest.getInProgressQuantity() - giftDonation.getQuantityDonated();
        giftRequest.setInProgressQuantity(Math.max(restoredQuantity, 0));

        // 查询与当前 giftRequest 相关的所有 donation
        List<GiftDonation> relatedDonations = giftDonationRepository.findByRequestId(giftRequest.getRequestId());

        // 如果没有其他 donation，则将 request 的状态设置为 PENDING
        if (relatedDonations.isEmpty()) {
            giftRequest.setStatus(GiftRequestStatus.PENDING);
        } else {
            // 检查 donation 的状态
            boolean allReceived = relatedDonations.stream()
                    .allMatch(donation -> donation.getDonationStatus() == GiftDonationStatus.RECEIVED);

            if (allReceived) {
                // 如果所有 donation 都是 RECEIVED，则将 request 的状态设置为 PENDING
                giftRequest.setStatus(GiftRequestStatus.PENDING);
            } else {
                // 如果有任何 donation 的状态不是 RECEIVED，request 仍保持 IN_PROGRESS
                giftRequest.setStatus(GiftRequestStatus.IN_PROGRESS);
            }
        }

        // 保存更新后的 GiftRequest
        giftRequestRepository.save(giftRequest);
    }

    @Override
    public void editDonation(UUID donationId, UUID donorSub, ModifyGiftDonationDto modifyGiftDonationDto) {
        GiftDonation giftDonation = getSpecificDonationDonor(donationId, donorSub);
        if(giftDonation.getDonationStatus() != GiftDonationStatus.PENDING) {
            log.error("donationStatus can only be edited in pending status");
            throw BaseException.builder()
                    .returnCode(ReturnCode.INVALID_DONATION_STATUS).build();
        }
        UpdateUtil.updateIfPresent(modifyGiftDonationDto.getQuantityDonated(), giftDonation::setQuantityDonated);
        giftDonationRepository.save(giftDonation);

    }

    @Override
    public Page<GiftDonationDashboardDto> getDashboardDonations(UUID donorSub, Pageable pageable, String keyword, GiftDonationStatus status) {
        Page<GiftDonation> donations;

        if (keyword != null && status != null) {
            donations = giftDonationRepository.findByDonorSubAndGiftNameContainingAndDonationStatus(
                    donorSub, keyword, status, pageable);
        } else if (keyword != null) {
            donations = giftDonationRepository.findByDonorSubAndGiftNameContaining(
                    donorSub, keyword, pageable);
        } else if (status != null) {
            donations = giftDonationRepository.findByDonorSubAndDonationStatus(
                    donorSub, status, pageable);
        } else {
            donations = giftDonationRepository.findByDonorSub(donorSub, pageable);
        }

        return donations.map(this::convertToGiftDonationDashboardDto);
    }

    private GiftDonationDashboardDto convertToGiftDonationDashboardDto(GiftDonation donation) {
        return GiftDonationDashboardDto.builder()
                .donationId(donation.getDonationId())
                .giftName(donation.getGiftName())
                .donationStatus(donation.getDonationStatus())
                .quantityDonated(donation.getQuantityDonated())
                .receiverSub(donation.getReceiverSub())
                .donationCreated(donation.getDonationCreated())
                .donationUpdated(donation.getDonationUpdated())
                .build();
    }

    @Override
    public GiftDonation getSpecificDonationDonor(UUID donationId, UUID donorSub) {
        GiftDonation giftDonation = giftDonationRepository.findById(donationId).orElseThrow(() -> {
            log.error("cannot find donationId {}", donationId);
            return BaseException.builder()
                    .returnCode(ReturnCode.RC400)
                    .build();
        });

        if (!donorSub.equals(giftDonation.getDonorSub())) {
            log.error("Invalid user access for donationId {}", donationId);
            throw BaseException.builder()
                    .returnCode(ReturnCode.INVALID_USER)
                    .build();
        }
        return giftDonation;
    }

    @Override
    public void updateTrackingNumber(UUID donationId, ModifyGiftDonationDto modifyGiftDonationDto, UUID donorSub) {
        GiftDonation giftDonation = getSpecificDonationDonor(donationId,donorSub);
        if(!giftDonation.getDonationStatus().equals(GiftDonationStatus.PROCESSING)){
            log.error("cannot not update tracking number now");
            throw BaseException.builder()
                    .returnCode(ReturnCode.RC400)
                    .message("trackingNumber can only be updated when status is PROCESSING").build();
        }
        String trackingNumber = modifyGiftDonationDto.getTrackingNumber();
        if(trackingNumber != null && !trackingNumber.isEmpty()) {
            giftDonation.setTrackingNumber(trackingNumber);
            giftDonation.setDonationStatus(GiftDonationStatus.SHIPPING);
            giftDonationRepository.save(giftDonation);

            // Notification for recipient or care agency
            // construct message
            Map<String, Object> donorParameters = new HashMap<>();
            donorParameters.put("requestId", giftDonation.getRequestId().toString());
            donorParameters.put("donorSub", giftDonation.getDonorSub().toString());
            donorParameters.put("donationId", giftDonation.getDonationId().toString());
            donorParameters.put("trackingNumber", trackingNumber);

            sendMessageService.sendNotification(MessageType.DONATION_SHIPPED,giftDonation.getReceiverSub(),donorParameters,"notification.donation.shipped");

        }else{
            log.error("trackingNumber is empty");
            throw BaseException.builder()
                    .returnCode(ReturnCode.RC400)
                    .message("trackingNumber is empty").build();
        }
    }

}
