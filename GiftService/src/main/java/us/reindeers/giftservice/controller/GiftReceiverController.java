package us.reindeers.giftservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import us.reindeers.giftservice.domain.dto.*;
import us.reindeers.giftservice.domain.entity.GiftDonationStatus;
import us.reindeers.giftservice.domain.entity.GiftRequestStatus;
import us.reindeers.giftservice.domain.entity.RequestType;
import us.reindeers.giftservice.domain.model.DonationReview;
import us.reindeers.giftservice.domain.model.SubReview;
import us.reindeers.giftservice.service.GiftReceiverService;

import java.util.UUID;

@RestController
@RequestMapping("/api/gifts/receiver")
@RequiredArgsConstructor
@Validated
public class GiftReceiverController {

    private final GiftReceiverService giftReceiverService;

    /*
    * 创建一个新的礼物请求
    * */
    @PostMapping("/request/new")
    public UUID createGiftRequest(@RequestBody ModifyGiftRequestDto modifyGiftRequestDto,
                                  @AuthenticationPrincipal Jwt jwt){
        UUID receiverSub = UUID.fromString(jwt.getClaimAsString("sub"));
        String requestTypeStr = jwt.getClaimAsStringList("cognito:groups").get(0);
        RequestType requestType = RequestType.valueOf(requestTypeStr.toUpperCase());
        return giftReceiverService.createRequest(modifyGiftRequestDto, receiverSub, requestType);
    }

    /*
    * 取消一个礼物请求
    * */
    @PostMapping("/request/cancel/{requestId}")
    public void cancelGiftRequest(@PathVariable UUID requestId, @AuthenticationPrincipal Jwt jwt){
        UUID receiverSub = UUID.fromString(jwt.getClaimAsString("sub"));
        giftReceiverService.cancelRequest(requestId, receiverSub);
    }

    /*
    * 查看某个请求的详细信息
    * */
    @GetMapping("/request/{requestId}")
    public GiftRequestDetailDto getSpecificRequest(@PathVariable UUID requestId, @AuthenticationPrincipal Jwt jwt){
        UUID receiverSub = UUID.fromString(jwt.getClaimAsString("sub"));
        return giftReceiverService.getSpecificRequest(requestId, receiverSub);
    }

    /*
    * 查询某个请求下所有donation的信息
    * */
    @GetMapping("/request/{requestId}/donations")
    public Page<GiftDonationSummaryDto> getGiftDonationsForSpecificRequest(
            @PathVariable UUID requestId,
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        UUID receiverSub = UUID.fromString(jwt.getClaimAsString("sub"));
        return giftReceiverService.getGiftDonationsForSpecificRequest(requestId, receiverSub, page, size);
    }

    /*
    * 更新某一个礼物请求
    * */
    @PostMapping("/request/update/{requestId}")
    public void updateRequest(@PathVariable UUID requestId,
                              @AuthenticationPrincipal Jwt jwt,
                              @RequestBody ModifyGiftRequestDto modifyGiftRequestDto){
        UUID receiverSub = UUID.fromString(jwt.getClaimAsString("sub"));
        giftReceiverService.editRequest(modifyGiftRequestDto,receiverSub,requestId);
    }

    /*
     * receiver confirm某个捐赠订单 表示捐赠流程正式开始
     * */
    @PatchMapping("/donation/confirm/{donationId}")
    public void confirmDonation(@PathVariable UUID donationId,
                                @AuthenticationPrincipal Jwt jwt){
        UUID receiverSub = UUID.fromString(jwt.getClaimAsString("sub"));
        giftReceiverService.confirmDonation(donationId,receiverSub);
    }

    /*
     * 确认收货 完成某个捐赠流程
     * */
    @PatchMapping("/donation/receive/{donationId}")
    public void completeDonation(@PathVariable UUID donationId,
                                 @AuthenticationPrincipal Jwt jwt){
        UUID receiverSub = UUID.fromString(jwt.getClaimAsString("sub"));
        giftReceiverService.receiveDonation(donationId,receiverSub);
    }

    /*
     * 申请取消某个donation
     * */
    @PatchMapping("/donation/cancel/{donationId}")
    public void cancelDonation(@PathVariable UUID donationId,
                               @AuthenticationPrincipal Jwt jwt){
        UUID receiverSub = UUID.fromString(jwt.getClaimAsString("sub"));
        giftReceiverService.cancelDonation(donationId,receiverSub);
    }

    /*
    * 查询当前receiver收到的所有donation
    * filter  keyword status
    * 分页*/
    @GetMapping("/received-donation")
    public Page<GiftDonationDashboardDto> getDashboardDonations(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) GiftDonationStatus status,
            @AuthenticationPrincipal Jwt jwt
    ) {
        UUID receiverSub = UUID.fromString(jwt.getClaimAsString("sub"));
        Pageable pageable = PageRequest.of(page, size, Sort.by("donationCreated").descending());
        return giftReceiverService.getDashboardDonations(receiverSub, pageable, keyword, status);
    }

    /*
     * 查找当前receiver用户的所有礼物请求
     * 分页
     * filter status keyword
     * */
    @GetMapping("/posted-request")
    public Page<GiftRequestDashboardDto> getDashboardRequests(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) GiftRequestStatus status,
            @AuthenticationPrincipal Jwt jwt
    ) {
        UUID receiverSub = UUID.fromString(jwt.getClaimAsString("sub"));
        Pageable pageable = PageRequest.of(page, size, Sort.by("requestCreated").descending());
        return giftReceiverService.getDashboardRequests(receiverSub, pageable, keyword, status);
    }

    /*
    * 填写review
    * */
    @PostMapping("/donation/{donationId}/review")
    public void createDonationReview(
            @PathVariable UUID donationId,
            @Valid @RequestBody DonationReview donationReview,
            @AuthenticationPrincipal Jwt jwt) {

        UUID reviewerSub = UUID.fromString(jwt.getClaimAsString("sub"));
        giftReceiverService.createDonationReview(donationId, donationReview, reviewerSub);
    }



    /*
     * 删除Review By ID
     * */
    @DeleteMapping("/donation/{donationId}/review")
    public void deleteDonationReview(
            @PathVariable UUID donationId,
            @AuthenticationPrincipal Jwt jwt
    ){
        UUID reviewerSub = UUID.fromString(jwt.getClaimAsString("sub"));
        giftReceiverService.deleteDonationReview(donationId,reviewerSub);

    }

    /*
     * 填写SubReview
     * */
    @PostMapping ("/donation/{donationId}/subReview")
    public void createSubReview(
            @PathVariable UUID donationId,
            @Valid @RequestBody SubReview donationSubReview,
            @AuthenticationPrincipal Jwt jwt
    ) {
        UUID reviewerSub = UUID.fromString(jwt.getClaimAsString("sub"));
        giftReceiverService.createSubDonationReview(donationId,donationSubReview,reviewerSub);
    }
    


    /*
     * 删除SubReview By ID
     * */
    @DeleteMapping("/donation/{donationId}/subReview/{subReviewId}")
    public void deleteDonationReview(
            @PathVariable UUID donationId,
            @PathVariable UUID subReviewId,
            @AuthenticationPrincipal Jwt jwt
    ){
        UUID reviewerSub = UUID.fromString(jwt.getClaimAsString("sub"));
        giftReceiverService.deleteSubDonationReview(donationId,subReviewId,reviewerSub);

    }



}
