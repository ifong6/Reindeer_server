package us.reindeers.giftservice.domain.dto;

import lombok.Data;
import us.reindeers.giftservice.domain.entity.ChildInfo;
import us.reindeers.giftservice.domain.entity.GiftCategory;

import java.util.List;

/*
 * 用于传递GiftRequest信息
 * 用在创建、修改Request
 * */

@Data
public class ModifyGiftRequestDto {
    private String giftName;
    private Integer quantity;
    private String description;
    private List<String> images;
    private GiftCategory giftCategory;
    private Integer estimatedPrice;
    private ChildInfo childInfo;
}
