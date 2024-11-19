package us.reindeers.giftservice.repository.specifications;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import us.reindeers.giftservice.domain.entity.GiftCategory;
import us.reindeers.giftservice.domain.entity.GiftRequest;
import us.reindeers.giftservice.domain.entity.GiftRequestStatus;
import us.reindeers.giftservice.domain.entity.RequestType;

import java.util.ArrayList;
import java.util.List;

public class GiftRequestSpecifications {

    public static Specification<GiftRequest> searchGiftRequests(
            String keyword,
            Integer minUnitPrice,
            Integer maxUnitPrice,
            List<GiftCategory> categories,
            List<RequestType> requestTypes,
            List<GiftRequestStatus> statuses,
            List<GiftRequestStatus> excludedStatuses) {

        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 关键字条件
            if (keyword != null && !keyword.isEmpty()) {
                predicates.add(cb.like(root.get("giftName"), "%" + keyword + "%"));
            }

            // 最小单价条件
            if (minUnitPrice != null) {
                predicates.add(cb.greaterThanOrEqualTo(
                        cb.quot(root.get("estimatedPrice"), root.get("quantity")).as(Double.class),
                        cb.literal(minUnitPrice.doubleValue())));
            }

            // 最大单价条件
            if (maxUnitPrice != null) {
                predicates.add(cb.lessThanOrEqualTo(
                        cb.quot(root.get("estimatedPrice"), root.get("quantity")).as(Double.class),
                        cb.literal(maxUnitPrice.doubleValue())));
            }


            // 分类条件
            if (categories != null && !categories.isEmpty()) {
                predicates.add(root.get("giftCategory").in(categories));
            }

            // 请求类型条件
            if (requestTypes != null && !requestTypes.isEmpty()) {
                predicates.add(root.get("requestType").in(requestTypes));
            }

            // 状态条件：使用传入的statuses和默认的excludedStatuses
            if (statuses != null && !statuses.isEmpty()) {
                predicates.add(root.get("status").in(statuses));
            } else if (excludedStatuses != null && !excludedStatuses.isEmpty()) {
                predicates.add(root.get("status").in(excludedStatuses).not());
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
