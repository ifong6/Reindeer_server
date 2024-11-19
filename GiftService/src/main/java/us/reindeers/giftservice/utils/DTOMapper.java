package us.reindeers.giftservice.utils;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import us.reindeers.giftservice.domain.dto.*;
import us.reindeers.giftservice.domain.entity.GiftDonation;
import us.reindeers.giftservice.domain.entity.GiftRequest;
import us.reindeers.common.response.constant.template.ReturnCode;
import us.reindeers.common.response.exception.BaseException;

@Service
@RequiredArgsConstructor
@Slf4j
public class DTOMapper {

  private final RequestUserServiceUtil requestUserServiceUtil;

  public GiftRequestDetailDto buildGiftRequestDetailDto(GiftRequest giftRequest) {
    Map receiver = requestUserServiceUtil.getUserDetailedInfo(giftRequest.getReceiverSub());
    receiver.remove("email");
    receiver.remove("address");
    receiver.remove("accountCreated");
    return new GiftRequestDetailDto(giftRequest, receiver);
  }

  public List<GiftRequestSummaryDto> buildGiftRequestSummaryDtoList(List<GiftRequest> giftRequests) {
    List<GiftRequestSummaryDto> giftRequestList = new ArrayList<>();
    for (GiftRequest giftRequest : giftRequests) {
      // Retrieve the receiver detail from UserService
      Map receiver = requestUserServiceUtil.getUserDetailedInfo(giftRequest.getReceiverSub());

      // 获取 data 部分
      Map<String, Object> receiverData = (Map<String, Object>) receiver.get("data");

      // 检查 receiverData 是否存在并从中获取字段
      String username = receiverData != null ? receiverData.getOrDefault("username", "").toString() : "";

      giftRequestList.add(createFromGiftRequest(giftRequest, username));
    }
    return giftRequestList;
  }

  public Page<GiftRequestSummaryDto> buildGiftRequestSummaryDtoPage(Page<GiftRequest> giftRequests) {
    return giftRequests.map(giftRequest -> {
      // Retrieve the receiver detail from UserService
      Map receiver = requestUserServiceUtil.getUserDetailedInfo(giftRequest.getReceiverSub());

      // 获取 data 部分
      Map<String, Object> receiverData = (Map<String, Object>) receiver.get("data");

      // 检查 receiverData 是否存在并从中获取字段
      String username = receiverData != null ? receiverData.getOrDefault("username", "").toString() : "";

      // 使用 DTO 映射逻辑
      return createFromGiftRequest(giftRequest, username);
    });
  }

  public GiftDonationSummaryDto buildGiftDonationSummaryDto(String role, GiftDonation giftDonation, GiftRequest giftRequest) {
    if (!giftDonation.getRequestId().equals(giftRequest.getRequestId())) {
      log.error("Failed to map GiftDonation to GiftDonationSummaryDto: Given giftRequest is not related with given giftDonation");
      throw BaseException.builder()
              .returnCode(ReturnCode.INVALID_INPUT)
              .build();
    }

    // only when
    // 获取 receiver 和 donor 的详细信息
    Map<String, Object> receiverDetails = (Map<String, Object>) requestUserServiceUtil.getUserDetailedInfo(giftDonation.getReceiverSub()).get("data");
    Map<String, Object> donorDetails = (Map<String, Object>) requestUserServiceUtil.getUserDetailedInfo(giftDonation.getDonorSub()).get("data");

    // 获取 address 信息，当 giftDonation 被确认时
    String address = null;
    if (giftDonation.getIsConfirmed() && receiverDetails != null) {
      address = receiverDetails.getOrDefault("address", "").toString();
    }

    return GiftDonationSummaryDto.builder()
            .donationId(giftDonation.getDonationId())
            .requestId(giftDonation.getRequestId())
            .giftName(giftDonation.getGiftName())
            .images(giftRequest.getImages())
            .giftCategory(giftRequest.getGiftCategory())
            .quantityDonated(giftDonation.getQuantityDonated())
            .donorUsername(donorDetails != null ? donorDetails.getOrDefault("username", "").toString() : "")
            .donorSub(giftDonation.getDonorSub())
            .receiverUsername(receiverDetails != null ? receiverDetails.getOrDefault("username", "").toString() : "")
            .receiverSub(giftDonation.getReceiverSub())
            .donationStatus(giftDonation.getDonationStatus())
            .trackingNumber(giftDonation.getTrackingNumber())
            .donationCreated(giftDonation.getDonationCreated())
            .donationUpdated(giftDonation.getDonationUpdated())
            .isConfirmed(giftDonation.getIsConfirmed())
            .confirmationTime(giftDonation.getConfirmationTime())
            // 当角色是 "donor" 时，返回 address
            .address(Objects.equals(role, "donor") ? address : null)
            .build();
  }

  public List<GiftDonationSummaryDto> buildGiftDonationSummaryDtoList(String role, Map<GiftDonation, GiftRequest> donationToRequest) {
    List<GiftDonationSummaryDto> giftDonationSummaryDtoList = new ArrayList<>();
    for (GiftDonation giftDonation : donationToRequest.keySet()) {
      giftDonationSummaryDtoList.add(buildGiftDonationSummaryDto(role, giftDonation, donationToRequest.get(giftDonation)));
    }
    return giftDonationSummaryDtoList;
  }

  public static GiftRequestSummaryDto createFromGiftRequest(GiftRequest giftRequest, String receiverUsername) {
    int neededQuantity = calculateNeededQuantity(giftRequest);
    int estimatedPrice = calculateEstimatedPrice(giftRequest, neededQuantity);

    return GiftRequestSummaryDto.builder()
            .receiverUsername(receiverUsername)
            .requestId(giftRequest.getRequestId())
            .requestType(giftRequest.getRequestType())
            .giftName(giftRequest.getGiftName())
            .giftCategory(giftRequest.getGiftCategory())
            .neededQuantity(neededQuantity)
            .images(giftRequest.getImages())
            .estimatedPrice(estimatedPrice)
            .status(giftRequest.getStatus())
            .build();
  }

  private static int calculateNeededQuantity(GiftRequest giftRequest) {
    return Math.max(0, giftRequest.getQuantity() - giftRequest.getInProgressQuantity() - giftRequest.getReceivedQuantity());
  }

  private static int calculateEstimatedPrice(GiftRequest giftRequest, int neededQuantity) {
    return giftRequest.getEstimatedPrice() * neededQuantity / giftRequest.getQuantity();
  }
}
