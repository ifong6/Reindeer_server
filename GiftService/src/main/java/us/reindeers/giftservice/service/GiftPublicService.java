package us.reindeers.giftservice.service;

import org.springframework.data.domain.Page;
import us.reindeers.giftservice.domain.dto.GiftRequestDetailDto;
import us.reindeers.giftservice.domain.dto.GiftRequestSummaryDto;
import us.reindeers.giftservice.domain.entity.GiftCategory;
import us.reindeers.giftservice.domain.entity.GiftRequestStatus;
import us.reindeers.giftservice.domain.entity.RequestType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface GiftPublicService {

    Page<GiftRequestSummaryDto> getGiftRequests(int page, int size);

    Page<GiftRequestSummaryDto> searchGiftRequestsContaining(
            int page, int size, String keyword, List<RequestType> requestTypes,
            List<GiftRequestStatus> statuses, Integer minUnitPrice, Integer maxUnitPrice,
            List<GiftCategory> categories, UUID receiverSub, LocalDateTime startDate,
            LocalDateTime endDate, String sortOrder);

    GiftRequestDetailDto getGiftRequestInfo(UUID requestId);
}
