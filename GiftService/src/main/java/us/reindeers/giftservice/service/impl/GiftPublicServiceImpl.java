package us.reindeers.giftservice.service.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import us.reindeers.giftservice.domain.dto.GiftRequestDetailDto;
import us.reindeers.giftservice.domain.dto.GiftRequestSummaryDto;
import us.reindeers.giftservice.domain.entity.GiftCategory;
import us.reindeers.giftservice.domain.entity.GiftRequest;
import us.reindeers.giftservice.domain.entity.GiftRequestStatus;
import us.reindeers.giftservice.domain.entity.RequestType;
import us.reindeers.giftservice.repository.GiftRequestRepository;
import us.reindeers.giftservice.repository.specifications.GiftRequestSpecifications;
import us.reindeers.giftservice.service.GiftPublicService;
import us.reindeers.giftservice.utils.DTOMapper;
import us.reindeers.common.response.constant.template.ReturnCode;
import us.reindeers.common.response.exception.BaseException;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
@Slf4j
public class GiftPublicServiceImpl implements GiftPublicService {

    private GiftRequestRepository giftRequestRepository;
    private final DTOMapper dtoMapper;

    private final List<GiftRequestStatus> EXCLUDED_STATUSES = Arrays.asList(
            GiftRequestStatus.CANCELLED,
            GiftRequestStatus.FULFILLED_IN_PROGRESS,
            GiftRequestStatus.COMPLETED
    );


    @Override
    public Page<GiftRequestSummaryDto> getGiftRequests(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("requestUpdated").descending());
        Page<GiftRequest> giftRequestsPage = giftRequestRepository.findActiveGiftRequests(pageable);

        // Convert the Page<GiftRequest> to Page<GiftRequestSummaryDto>
        List<GiftRequestSummaryDto> giftRequestSummaryDtoList = dtoMapper.buildGiftRequestSummaryDtoList(giftRequestsPage.getContent());

        return new PageImpl<>(giftRequestSummaryDtoList, pageable, giftRequestsPage.getTotalElements());
    }

    @Override
    public Page<GiftRequestSummaryDto> searchGiftRequestsContaining(
            int page, int size, String keyword, List<RequestType> requestTypes,
            List<GiftRequestStatus> statuses, Integer minUnitPrice, Integer maxUnitPrice,
            List<GiftCategory> categories, UUID receiverSub, LocalDateTime startDate,
            LocalDateTime endDate, String sortOrder) {

        Sort sort = Sort.by("requestUpdated");
        sort = "asc".equalsIgnoreCase(sortOrder) ? sort.ascending() : sort.descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        // 如果用户未指定 statuses，则使用默认排除状态
        List<GiftRequestStatus> effectiveExcludedStatuses = (statuses == null || statuses.isEmpty())
                ? EXCLUDED_STATUSES
                : Collections.emptyList();

        // 构建动态 Specification
        Specification<GiftRequest> specification = Specification.where(
                GiftRequestSpecifications.searchGiftRequests(
                        keyword, minUnitPrice, maxUnitPrice, categories, requestTypes,
                        statuses, effectiveExcludedStatuses)
        );

        // 使用 Specification 执行查询
        Page<GiftRequest> giftRequestsPage = giftRequestRepository.findAll(specification, pageable);

        // 转换结果到 DTO 列表
        List<GiftRequestSummaryDto> giftRequestSummaryDtoList =
                dtoMapper.buildGiftRequestSummaryDtoList(giftRequestsPage.getContent());

        return new PageImpl<>(giftRequestSummaryDtoList, pageable, giftRequestsPage.getTotalElements());
    }


    @Override
    public GiftRequestDetailDto getGiftRequestInfo(UUID requestId) {

        // Retrieve the request from the repository
        GiftRequest giftRequest = giftRequestRepository.findByRequestId(requestId).orElseThrow(() -> {
            log.error("cannot find requestId {}", requestId);
            return BaseException.builder()
                    .returnCode(ReturnCode.REQUEST_NOT_EXIST)
                    .message("Gift request not found for ID: " + requestId)
                    .build();
        });

        return dtoMapper.buildGiftRequestDetailDto(giftRequest);
    }
}
