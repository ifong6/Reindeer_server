package us.reindeers.giftservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import us.reindeers.giftservice.domain.dto.GiftDonationDashboardDto;
import us.reindeers.giftservice.domain.dto.GiftDonationSummaryDto;
import us.reindeers.giftservice.domain.dto.ModifyGiftDonationDto;
import us.reindeers.giftservice.domain.entity.GiftDonationStatus;
import us.reindeers.giftservice.service.GiftDonorService;

import java.util.UUID;

@RestController
@RequestMapping("/api/gifts/donor")
@RequiredArgsConstructor
public class GiftDonorController {

    private final GiftDonorService giftDonorService;

    /*
    * 根据某个礼物需求发起一次新的捐赠
    * */
    @PostMapping("/donation/new/{requestId}")
    public void createGiftDonation(@RequestBody ModifyGiftDonationDto modifyGiftDonationDto,
                                               @PathVariable UUID requestId,
                                               @AuthenticationPrincipal Jwt jwt){
        UUID donorSub = UUID.fromString(jwt.getClaimAsString("sub"));
        giftDonorService.createDonation(modifyGiftDonationDto, requestId, donorSub);
    }

    /*
    * 取消某个捐赠申请
    * */
    @PatchMapping("/donation/cancel/{donationId}")
    public void cancelGiftDonation(@PathVariable UUID donationId, @AuthenticationPrincipal Jwt jwt){
        UUID donorSub = UUID.fromString(jwt.getClaimAsString("sub"));
        giftDonorService.cancelDonation(donationId, donorSub);
    }

    /*
    * 更新某个礼物申请
    * */
    @PostMapping("/donation/update/{donationId}")
    public void updateGiftDonation(@PathVariable UUID donationId,
                                   @AuthenticationPrincipal Jwt jwt,
                                   @RequestBody ModifyGiftDonationDto modifyGiftDonationDto){
        UUID donorSub = UUID.fromString(jwt.getClaimAsString("sub"));
        giftDonorService.editDonation(donationId, donorSub, modifyGiftDonationDto);
    }

    /*
    * 查询当前用户的所有捐赠订单
    * filter keyword status 有分页
    * */
    @GetMapping("/handled-donation")
    public Page<GiftDonationDashboardDto> getDonorDashboardDonations(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) GiftDonationStatus status,
            @AuthenticationPrincipal Jwt jwt
    ) {
        UUID donorSub = UUID.fromString(jwt.getClaimAsString("sub"));
        Pageable pageable = PageRequest.of(page, size, Sort.by("donationCreated").descending());
        return giftDonorService.getDashboardDonations(donorSub, pageable, keyword, status);
    }

    /*
    * 添加快递单号
    * */
    @PatchMapping("/donation/{donationId}")
    public void updateTrackingNumber(@PathVariable UUID donationId,
                                     @RequestBody ModifyGiftDonationDto modifyGiftDonationDto,
                                     @AuthenticationPrincipal Jwt jwt){
        UUID donorSub = UUID.fromString(jwt.getClaimAsString("sub"));
        giftDonorService.updateTrackingNumber(donationId, modifyGiftDonationDto, donorSub);
    }
}
