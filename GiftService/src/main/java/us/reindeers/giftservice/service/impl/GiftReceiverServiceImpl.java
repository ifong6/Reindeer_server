package us.reindeers.giftservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import us.reindeers.common.enums.MessageType;
import us.reindeers.giftservice.domain.dto.*;
import us.reindeers.giftservice.domain.entity.*;
import us.reindeers.giftservice.domain.model.DonationReview;
import us.reindeers.giftservice.domain.model.SubReview;
import us.reindeers.giftservice.repository.GiftDonationRepository;
import us.reindeers.giftservice.repository.GiftRequestRepository;
import us.reindeers.giftservice.service.GiftReceiverService;
import us.reindeers.giftservice.service.ReviewService;
import us.reindeers.giftservice.service.SendMessageService;
import us.reindeers.giftservice.utils.DTOMapper;
import us.reindeers.giftservice.utils.GiftServiceHelper;
import us.reindeers.giftservice.utils.UpdateUtil;
import us.reindeers.common.response.constant.template.ReturnCode;
import us.reindeers.common.response.exception.BaseException;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class GiftReceiverServiceImpl implements GiftReceiverService {

    private final GiftRequestRepository giftRequestRepository;
    private final GiftDonationRepository giftDonationRepository;
    private final GiftServiceHelper giftServiceHelper;
    private final SendMessageService sendMessageService;
    private final DTOMapper dtoMapper;
    private final ReviewService reviewService;


    @Override
    public UUID createRequest(ModifyGiftRequestDto modifyGiftRequestDto, UUID receiverSub, RequestType requestType) {
        GiftRequest giftRequest = GiftRequest.builder()
                .quantity(modifyGiftRequestDto.getQuantity())
                .receiverSub(receiverSub)
                .requestType(requestType)
                .giftName(modifyGiftRequestDto.getGiftName())
                .estimatedPrice(modifyGiftRequestDto.getEstimatedPrice())
                .description(modifyGiftRequestDto.getDescription())
                .images(modifyGiftRequestDto.getImages())
                .giftCategory(modifyGiftRequestDto.getGiftCategory())
                .inProgressQuantity(0)
                .receivedQuantity(0)
                .childInfo(requestType == RequestType.CARE_AGENCY ? modifyGiftRequestDto.getChildInfo() : null)
                .status(GiftRequestStatus.PENDING).build();

        giftRequest = giftRequestRepository.save(giftRequest);
        UUID requestId = giftRequest.getRequestId();

        // construct message
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("requestId", requestId.toString());

        sendMessageService.sendNotification(MessageType.REQUEST_PUBLISHED,receiverSub,parameters,"notification.gift.created");
        return requestId;
    }

    @Override
    public void cancelRequest(UUID requestId, UUID receiverSub) {
        GiftRequest giftRequest = giftServiceHelper.getSpecificGiftRequest(requestId);
        GiftRequestStatus status = giftRequest.getStatus();
        if(status != GiftRequestStatus.PENDING){
            log.error("if to cancel request, status must be PENDING");
            throw BaseException.builder()
                    .returnCode(ReturnCode.INVALID_REQUEST_STATUS)
                    .build();
        }

        giftRequest.setStatus(GiftRequestStatus.CANCELLED);
        giftRequestRepository.save(giftRequest);
    }

    @Override
    public void editRequest(ModifyGiftRequestDto modifyGiftRequestDto, UUID receiverSub, UUID requestId) {
        GiftRequest giftRequest = giftServiceHelper.getSpecificGiftRequest(requestId);
        GiftRequestStatus status = giftRequest.getStatus();
        if(!status.equals(GiftRequestStatus.PENDING) && !status.equals(GiftRequestStatus.IN_PROGRESS)) {
            log.error("requestStatus shall be Pending or In_Progress");
            throw BaseException.builder()
                    .returnCode(ReturnCode.INVALID_REQUEST_STATUS)
                    .build();
        }
        if(modifyGiftRequestDto == null){
            log.error("GiftRequestDTO is null");
            throw new IllegalArgumentException("GiftRequestDTO cannot be null");
        }

        UpdateUtil.updateIfPresentAndNotEmpty(modifyGiftRequestDto.getGiftName(), giftRequest::setGiftName);
        UpdateUtil.updateIfPresentAndNotEmpty(modifyGiftRequestDto.getDescription(), giftRequest::setDescription);
        UpdateUtil.updateIfPresent(modifyGiftRequestDto.getQuantity(), giftRequest::setQuantity);
        UpdateUtil.updateIfPresent(modifyGiftRequestDto.getEstimatedPrice(), giftRequest::setEstimatedPrice);
        UpdateUtil.updateIfPresentAndNotEmpty(modifyGiftRequestDto.getImages(), giftRequest::setImages);
        UpdateUtil.updateIfPresent(modifyGiftRequestDto.getGiftCategory(), giftRequest::setGiftCategory);
        UpdateUtil.updateIfPresent(modifyGiftRequestDto.getChildInfo(), giftRequest::setChildInfo);

        giftRequestRepository.save(giftRequest);
    }

    @Override
    public GiftRequestDetailDto getSpecificRequest(UUID requestId, UUID receiverSub) {
        GiftRequest giftRequest = giftServiceHelper.getSpecificGiftRequest(requestId);
        return dtoMapper.buildGiftRequestDetailDto(giftRequest);
    }

    @Override
    public Page<GiftDonationSummaryDto> getGiftDonationsForSpecificRequest(UUID requestId, UUID receiverSub, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("donationUpdated").descending());

        Page<GiftDonation> giftDonationsPage = giftDonationRepository.findAllByRequestId(requestId, pageable);

        if (giftDonationsPage.isEmpty()) {
            return Page.empty(pageable);
        }

        Map<GiftDonation, GiftRequest> donationToRequest = new HashMap<>();
        for (GiftDonation giftDonation : giftDonationsPage.getContent()) {
            donationToRequest.put(giftDonation, giftServiceHelper.getSpecificGiftRequest(giftDonation.getRequestId()));
        }

        List<GiftDonationSummaryDto> giftDonationDtos = dtoMapper.buildGiftDonationSummaryDtoList("receiver", donationToRequest);

        return new PageImpl<>(giftDonationDtos, pageable, giftDonationsPage.getTotalElements());
    }

    @Override
    public void createDonationReview(UUID donationId, DonationReview donationReview, UUID reviewerSub) {
        GiftDonation giftDonation = giftServiceHelper.getSpecificDonationBySub(donationId, reviewerSub);

        if (!giftDonation.getReceiverSub().equals(reviewerSub)) {
            throw BaseException.builder()
                    .returnCode(ReturnCode.INVALID_USER)
                    .message("User is not the receiver of this donation.")
                    .build();
        }

        if (giftDonation.getDonationStatus() != GiftDonationStatus.RECEIVED){
            throw BaseException.builder()
                    .returnCode(ReturnCode.INVALID_DONATION_STATUS)
                    .build();
        }

        // 设置 reviewerSub 和 reviewCreated
        donationReview.setDonationId(donationId);
        donationReview.setReviewerSub(reviewerSub);
        donationReview.setReviewCreated(LocalDateTime.now().toString());

        reviewService.createReview(donationReview);
    }



    @Override
    public DonationReview getDonationReview(UUID donationId) {
        return reviewService.getReview(donationId);
    }

    @Override
    public void deleteDonationReview(UUID donationId, UUID reviewerSub) {
        DonationReview donationReview = getDonationReview(donationId);
        if(!donationReview.getReviewerSub().equals(reviewerSub)){
            throw BaseException.builder()
                    .returnCode(ReturnCode.INVALID_USER)
                    .message("User is not the reviewer of this subreview.")
                    .build();
        }
        reviewService.deleteReview(donationId);
    }

    @Override
    public void createSubDonationReview(UUID donationId, SubReview subReview, UUID reviewerSub) {
        GiftDonation giftDonation = giftServiceHelper.getSpecificDonationBySub(donationId, reviewerSub);
        DonationReview donationReview = getDonationReview(donationId);

        if (donationReview == null) {
            throw BaseException.builder()
                    .returnCode(ReturnCode.REVIEW_NOT_FOUND)
                    .message("DonationReview not found for the provided donationId.")
                    .build();
        }

        if (!giftDonation.getReceiverSub().equals(reviewerSub) && !giftDonation.getDonorSub().equals(reviewerSub)){
            throw BaseException.builder()
                    .returnCode(ReturnCode.INVALID_USER)
                    .message("User is not the receiver or donor of this donation.")
                    .build();
        }

        if (giftDonation.getDonationStatus() != GiftDonationStatus.RECEIVED){
            throw BaseException.builder()
                    .returnCode(ReturnCode.INVALID_DONATION_STATUS)
                    .build();
        }

        reviewService.createSubReview(donationId,subReview);
    }

    @Override
    public SubReview getSubDonationReview(UUID donationId, UUID subReviewId) {
        return reviewService.getSubReview(donationId,subReviewId);
    }

    @Override
    public void deleteSubDonationReview(UUID donationId, UUID subReviewId, UUID reviewerSub) {
        SubReview subReview = getSubDonationReview(donationId,subReviewId);
        if(!subReview.getAuthorSub().equals(reviewerSub)){
            throw BaseException.builder()
                    .returnCode(ReturnCode.INVALID_USER)
                    .message("User is not the reviewer of this donation.")
                    .build();
        }
        reviewService.deleteSubReview(donationId,subReviewId);
    }

    @Override
    public Page<GiftRequestDashboardDto> getDashboardRequests(UUID receiverSub, Pageable pageable, String keyword, GiftRequestStatus status) {
        Page<GiftRequest> requests;

        if (keyword != null && status != null) {
            requests = giftRequestRepository.findByReceiverSubAndGiftNameContainingAndStatus(
                    receiverSub, keyword, status, pageable);
        } else if (keyword != null) {
            requests = giftRequestRepository.findByReceiverSubAndGiftNameContaining(
                    receiverSub, keyword, pageable);
        } else if (status != null) {
            requests = giftRequestRepository.findByReceiverSubAndStatus(
                    receiverSub, status, pageable);
        } else {
            requests = giftRequestRepository.findByReceiverSub(receiverSub, pageable);
        }

        return requests.map(this::convertToGiftRequestDashboardDto);
    }

    @Override
    public Page<GiftDonationDashboardDto> getDashboardDonations(UUID receiverSub, Pageable pageable, String keyword, GiftDonationStatus status) {
        Page<GiftDonation> donations;

        if (keyword != null && status != null) {
            donations = giftDonationRepository.findByReceiverSubAndGiftNameContainingAndDonationStatus(
                    receiverSub, keyword, status, pageable);
        } else if (keyword != null) {
            donations = giftDonationRepository.findByReceiverSubAndGiftNameContaining(
                    receiverSub, keyword, pageable);
        } else if (status != null) {
            donations = giftDonationRepository.findByReceiverSubAndDonationStatus(
                    receiverSub, status, pageable);
        } else {
            donations = giftDonationRepository.findByReceiverSub(receiverSub, pageable);
        }

        return donations.map(this::convertToGiftDonationDashboardDto);
    }

    private GiftDonationDashboardDto convertToGiftDonationDashboardDto(GiftDonation donation) {
        return GiftDonationDashboardDto.builder()
                .donationId(donation.getDonationId())
                .giftName(donation.getGiftName())
                .donationStatus(donation.getDonationStatus())
                .quantityDonated(donation.getQuantityDonated())
                .donorSub(donation.getDonorSub())
                .donationCreated(donation.getDonationCreated())
                .donationUpdated(donation.getDonationUpdated())
                .build();
    }

    private GiftRequestDashboardDto convertToGiftRequestDashboardDto(GiftRequest request) {
        return GiftRequestDashboardDto.builder()
                .requestId(request.getRequestId())
                .giftName(request.getGiftName())
                .status(request.getStatus())
                .neededQuantity(request.getQuantity())
                .receivedQuantity(request.getReceivedQuantity())
                .inProgressQuantity(request.getInProgressQuantity())
                .requestCreated(request.getRequestCreated())
                .requestUpdated(request.getRequestUpdated())
                .build();
    }

    @Override
    public void confirmDonation(UUID donationId, UUID receiverSub) {
        GiftDonation giftDonation = giftServiceHelper.getSpecificDonationBySub(donationId, receiverSub);

        if(giftDonation.getDonationStatus() != GiftDonationStatus.PENDING){
            throw BaseException.builder()
                    .returnCode(ReturnCode.INVALID_DONATION_STATUS)
                    .build();
        }else{
            giftDonation.setIsConfirmed(true);
            giftDonation.setDonationStatus(GiftDonationStatus.PROCESSING);
            giftDonation.setConfirmationTime(LocalDateTime.now());
            giftDonationRepository.save(giftDonation);

            //send message to donor
            // construct message
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("requestId", giftDonation.getRequestId().toString());
            parameters.put("receiverSub", giftDonation.getReceiverSub().toString());  // Request 发起者的 sub
            parameters.put("donationId", giftDonation.getDonationId().toString());

            sendMessageService.sendNotification(MessageType.DONATION_APPROVED,giftDonation.getDonorSub(),parameters,"notification.donation.approved");
        }
    }

    @Override
    public void receiveDonation(UUID donationId, UUID receiverSub){
        GiftDonation giftDonation = giftServiceHelper.getSpecificDonationBySub(donationId, receiverSub);
        giftDonation.setDonationStatus(GiftDonationStatus.RECEIVED);
        giftDonationRepository.save(giftDonation);

        UUID requestId = giftDonation.getRequestId();
        GiftRequest giftRequest = giftServiceHelper.getSpecificGiftRequest(requestId);
        Integer receivedQuantity = giftRequest.getReceivedQuantity();
        Integer quantityDonated = giftDonation.getQuantityDonated();

        giftRequest.setReceivedQuantity(receivedQuantity + quantityDonated);
        giftRequest.setInProgressQuantity(giftRequest.getInProgressQuantity() - quantityDonated);

        if(giftRequest.getReceivedQuantity().equals(giftRequest.getQuantity())){
            giftRequest.setStatus(GiftRequestStatus.COMPLETED);
        }else {
            // 查询与当前 giftRequest 相关的所有 donation
            List<GiftDonation> relatedDonations = giftDonationRepository.findByRequestId(giftRequest.getRequestId());

            // 如果没有其他 donation，将 request 的状态设置为 PENDING
            if (relatedDonations.isEmpty()) {
                giftRequest.setStatus(GiftRequestStatus.PENDING);
            } else {
                // 检查 donation 的状态
                boolean allReceived = relatedDonations.stream()
                        .allMatch(donation -> donation.getDonationStatus() == GiftDonationStatus.RECEIVED);

                if (allReceived) {
                    // 如果所有 donation 都是 RECEIVED，将 request 的状态设置为 PENDING
                    giftRequest.setStatus(GiftRequestStatus.PENDING);
                } else {
                    // 如果有 donation 的状态不是 RECEIVED，request 仍保持 IN_PROGRESS
                    giftRequest.setStatus(GiftRequestStatus.IN_PROGRESS);
                }
            }
        }
        giftRequestRepository.save(giftRequest);

        //send message to donor
        // construct message
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("requestId", giftDonation.getRequestId().toString());
        parameters.put("receiverSub", giftDonation.getReceiverSub().toString());  // Request 发起者的 sub
        parameters.put("donationId", giftDonation.getDonationId().toString());

        sendMessageService.sendNotification(MessageType.DONATION_COMPLETED,giftDonation.getDonorSub(),parameters,"notification.donation.completed");
    }

    @Override
    public void cancelDonation(UUID donationId, UUID receiverSub) {
        GiftDonation giftDonation = giftServiceHelper.getSpecificDonationBySub(donationId, receiverSub);
        GiftDonationStatus status = giftDonation.getDonationStatus();

        if (!status.equals(GiftDonationStatus.PENDING) &&
                !status.equals(GiftDonationStatus.PROCESSING) &&
                !status.equals(GiftDonationStatus.DONOR_CANCELLED)) {

            log.error("Donation can only be cancelled in pending, processing or donor cancelled status");
            throw BaseException.builder()
                    .returnCode(ReturnCode.INVALID_DONATION_STATUS)
                    .build();
        }

        if (status.equals(GiftDonationStatus.PENDING)) {

            giftDonation.setDonationStatus(GiftDonationStatus.CANCELLED);
            resetQuantityAfterCancel(giftDonation, giftRequestRepository.findByRequestId(giftDonation.getRequestId()).orElseThrow(() -> {
                log.error("cannot find requestId {}", giftDonation.getRequestId());
                return BaseException.builder()
                        .returnCode(ReturnCode.REQUEST_NOT_EXIST)
                        .build();
            }));

            notifyCancellation(giftDonation,giftDonation.getDonorSub(),"notification.donation.cancelled");

        } else if (status.equals(GiftDonationStatus.PROCESSING)) {
            giftDonation.setDonationStatus(GiftDonationStatus.RECEIVER_CANCELLED);
            notifyCancellationWithApproval(giftDonation,giftDonation.getDonorSub(),"notification.donation.cancelled.needs_approval");
        } else {
            giftDonation.setDonationStatus(GiftDonationStatus.CANCELLED);
            resetQuantityAfterCancel(giftDonation, giftRequestRepository.findByRequestId(giftDonation.getRequestId()).orElseThrow(() -> {
                log.error("cannot find requestId {}", giftDonation.getRequestId());
                return BaseException.builder()
                        .returnCode(ReturnCode.REQUEST_NOT_EXIST)
                        .build();
            }));
            notifyCancellation(giftDonation,giftDonation.getDonorSub(),"notification.donation.cancelled");
        }

        giftDonationRepository.save(giftDonation);
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

    private void resetQuantityAfterCancel(GiftDonation giftDonation, GiftRequest giftRequest){
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

}
