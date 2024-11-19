package us.reindeers.giftservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import us.reindeers.giftservice.domain.dto.GiftRequestDetailDto;
import us.reindeers.giftservice.domain.dto.GiftRequestSummaryDto;
import us.reindeers.giftservice.domain.entity.GiftCategory;
import us.reindeers.giftservice.domain.entity.GiftRequestStatus;
import us.reindeers.giftservice.domain.entity.RequestType;
import us.reindeers.giftservice.domain.model.DonationReview;
import us.reindeers.giftservice.domain.model.SubReview;
import us.reindeers.giftservice.service.GiftPublicService;
import us.reindeers.giftservice.service.GiftReceiverService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/gifts")
@RequiredArgsConstructor
public class GiftPublicController {

    private final GiftPublicService giftPublicService;
    private final GiftReceiverService giftReceiverService;

    /*
    * 主页显示礼物cards
    * 类似网上商城显示商品页
    * 无filter 分页
    * */
    @GetMapping("")
    public Page<GiftRequestSummaryDto> getGiftRequests(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size) {
        return giftPublicService.getGiftRequests(page, size);
    }

    /*
    * 根据filter显示对应的礼物cards
    * filter条件包括 keyword，发布者（福利院或个人），礼物申请状态（包括已完成 需求中等），价格区间
    * */
    @GetMapping("/search")
    public Page<GiftRequestSummaryDto> getGiftRequestByFilter(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(name = "request-types", required = false) List<String> requestTypes,
            @RequestParam(required = false) List<String> statuses,
            @RequestParam(name = "min-price", required = false) Integer minUnitPrice,
            @RequestParam(name = "max-price", required = false) Integer maxUnitPrice,
            @RequestParam(name = "categories", required = false) List<String> categories,
            @RequestParam(name = "receiverSub", required = false) UUID receiverSub,
            @RequestParam(name = "start-date", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(name = "end-date", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(name = "sortOrder", defaultValue = "desc") String sortOrder) {

        List<RequestType> upperCaseRequestTypes = requestTypes != null ? requestTypes.stream()
                .map(rt -> RequestType.valueOf(rt.toUpperCase()))
                .toList() : null;

        List<GiftRequestStatus> upperCaseStatuses = statuses != null ? statuses.stream()
                .map(st -> GiftRequestStatus.valueOf(st.toUpperCase()))
                .toList() : null;

        List<GiftCategory> upperCaseCategories = categories != null ? categories.stream()
                .map(c -> GiftCategory.valueOf(c.toUpperCase()))
                .toList() : null;

        Sort sort = Sort.by("requestUpdated");
        sort = "asc".equalsIgnoreCase(sortOrder) ? sort.ascending() : sort.descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        return giftPublicService.searchGiftRequestsContaining(
                page, size, keyword, upperCaseRequestTypes, upperCaseStatuses,
                minUnitPrice, maxUnitPrice, upperCaseCategories, receiverSub, startDate, endDate, sortOrder);
    }


    /*
    * 获取某个礼物的详细信息
    * */
    @GetMapping("/{requestId:[0-9a-fA-F\\\\-]{36}}")
    public GiftRequestDetailDto getGiftRequestInfo(@PathVariable UUID requestId) {
        return giftPublicService.getGiftRequestInfo(requestId);
    }

    /*
     * 获取Review By ID
     * */
    @GetMapping("/review/{donationId}")
    public DonationReview getDonationReview(
            @PathVariable UUID donationId
    ){
        DonationReview donationReview = giftReceiverService.getDonationReview(donationId);
        return donationReview;
    }

    /*
     * 获取SubReview By ID
     * */
    @GetMapping("/subReview/{donationId}/{subReviewId}")
    public SubReview getSubReview(
            @PathVariable UUID donationId,
            @PathVariable UUID subReviewId
    ) {
        return giftReceiverService.getSubDonationReview(donationId, subReviewId);
    }

}
