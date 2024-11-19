package us.reindeers.giftservice.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;
import us.reindeers.common.response.constant.template.ReturnCode;
import us.reindeers.common.response.exception.BaseException;
import us.reindeers.giftservice.domain.model.DonationReview;
import us.reindeers.giftservice.domain.model.SubReview;
import us.reindeers.giftservice.service.ReviewService;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ReviewServiceImpl implements ReviewService {

    private final DynamoDbClient dynamoDBClient;
    private static final String TABLE_NAME = "TestReviewTable";

    public ReviewServiceImpl() {
        // Use DefaultCredentialsProvider or any other provider you are using
        AwsCredentialsProvider credentialsProvider = DefaultCredentialsProvider.create();
        this.dynamoDBClient = DynamoDbClient.builder()
                .region(Region.US_WEST_2)
                .credentialsProvider(DefaultCredentialsProvider.create()) // Automatically loads credentials
                .build();
    }


    @Override
    public void createReview(DonationReview donationReview) {

        if (donationReview.getReviewCreated() == null) {
            donationReview.setReviewCreated(Instant.now().toString());
        }

        Map<String, AttributeValue> item = new HashMap<>();
        item.put("donationId", AttributeValue.builder().s(donationReview.getDonationId().toString()).build());
        item.put("reviewerSub", AttributeValue.builder().s(donationReview.getReviewerSub().toString()).build());
        item.put("reviewContent", AttributeValue.builder().s(donationReview.getReviewContent()).build());
        item.put("reviewCreated", AttributeValue.builder().s(donationReview.getReviewCreated()).build());

        if (donationReview.getReviewImageUrl() != null) {
            item.put("reviewImageUrl", AttributeValue.builder().s(donationReview.getReviewImageUrl()).build());
        }

        PutItemRequest request = PutItemRequest.builder()
                .tableName(TABLE_NAME)
                .item(item)
                .build();

        try {
            dynamoDBClient.putItem(request);
            log.info("Review with donationId {} created successfully.", donationReview.getDonationId());
        } catch (DynamoDbException e) {
            log.error("Failed to create review with donationId {}", donationReview.getDonationId(), e);
            throw BaseException.builder()
                    .returnCode(ReturnCode.RC500)
                    .message("Failed to create donation review with donationId " + donationReview.getDonationId())
                    .build();
        }
    }


    @Override
    public DonationReview getReview(UUID donationId) {

        Map<String, AttributeValue> key = new HashMap<>();
        key.put("donationId", AttributeValue.builder().s(donationId.toString()).build());

        GetItemRequest request = GetItemRequest.builder()
                .tableName(TABLE_NAME)
                .key(key)
                .build();
        GetItemResponse response = dynamoDBClient.getItem(request);

        Map<String, AttributeValue> item = response.item();

        if (item != null && !item.isEmpty()) {
            return convertItemToDonationReview(item);
        } else {
            log.error("Review not found with DonationID {}", donationId);
            throw BaseException.builder()
                    .returnCode(ReturnCode.REVIEW_NOT_FOUND)
                    .message("Unable to find donation review with id : " + donationId)
                    .build();
        }
    }

    @Override
    public void deleteReview(UUID donationId) {
        Map<String, AttributeValue> key = new HashMap<>();
        key.put("donationId", AttributeValue.builder().s(donationId.toString()).build());

        DeleteItemRequest deleteRequest = DeleteItemRequest.builder()
                .tableName(TABLE_NAME)
                .key(key)
                .build();

        try {
            dynamoDBClient.deleteItem(deleteRequest);
            log.info("Review with donationId " + donationId + " deleted successfully.");
        } catch (DynamoDbException e) {
            log.error("Failed to delete donation review with donationId {}", donationId, e);
            throw BaseException.builder()
                    .returnCode(ReturnCode.RC500)
                    .message("Failed to delete donation review with donationId " + donationId)
                    .build();
        }
    }

    @Override
    public void createSubReview(UUID donationId, SubReview subReview) {
        Map<String, AttributeValue> subReviewMap = new HashMap<>();
        subReviewMap.put("subReviewId", AttributeValue.builder().s(subReview.getSubReviewId().toString()).build());
        subReviewMap.put("content", AttributeValue.builder().s(subReview.getContent()).build());
        subReviewMap.put("authorSub", AttributeValue.builder().s(subReview.getAuthorSub().toString()).build());
        subReviewMap.put("timestamp", AttributeValue.builder().s(subReview.getTimestamp()).build());

        if (subReview.getSubReviewImageUrl() != null) {
            subReviewMap.put("subReviewImageUrl", AttributeValue.builder().s(subReview.getSubReviewImageUrl()).build());
        }

        Map<String, AttributeValue> key = new HashMap<>();
        key.put("donationId", AttributeValue.builder().s(donationId.toString()).build());

        UpdateItemRequest updateRequest = UpdateItemRequest.builder()
                .tableName(TABLE_NAME)
                .key(key)
                .updateExpression("SET subReviews = list_append(if_not_exists(subReviews, :emptyList), :newSubReview)")
                .expressionAttributeValues(Map.of(
                        ":newSubReview", AttributeValue.builder().l(AttributeValue.builder().m(subReviewMap).build()).build(),
                        ":emptyList", AttributeValue.builder().l(new ArrayList<>()).build()  // 用于处理 subReviews 为空的情况
                ))
                .build();

        try {
            dynamoDBClient.updateItem(updateRequest);
            log.info("SubReview with donationId " + donationId + " created successfully.");
        } catch (DynamoDbException e) {
            log.error("Failed to add sub-review to DonationReview with donationId {}", donationId, e);
            throw BaseException.builder()
                    .returnCode(ReturnCode.RC500)
                    .message("Failed to add sub-review to DonationReview with donationId " + donationId)
                    .build();
        }
    }


    @Override
    public SubReview getSubReview(UUID donationId, UUID subReviewId) {
        DonationReview donationReview = getReview(donationId);
        return donationReview.getSubReviews().stream()
                .filter(subReview -> subReview.getSubReviewId().equals(subReviewId))
                .findFirst()
                .orElseThrow(() -> {
                    log.error("SubReview not found with DonationID {} and SubReviewID {}", donationId, subReviewId);
                    return BaseException.builder()
                            .returnCode(ReturnCode.SUB_REVIEW_NOT_FOUND)
                            .message("unable to find the subview with id: " + subReviewId)
                            .build();
                });
    }


    @Override
    public void deleteSubReview(UUID donationId, UUID subReviewId) {
        // 获取 DonationReview
        DonationReview donationReview = getReview(donationId);

        // 检查并找到 subReviewId 是否存在于 subReviews 列表中
        List<SubReview> subReviews = donationReview.getSubReviews();
        boolean removed = subReviews.removeIf(subReview -> subReview.getSubReviewId().equals(subReviewId));

        if (!removed) {
            log.error("SubReview not found with DonationID {} and SubReviewID {}", donationId, subReviewId);
            throw BaseException.builder()
                    .returnCode(ReturnCode.SUB_REVIEW_NOT_FOUND)
                    .message("unable to find the subview with id: " + subReviewId)
                    .build();
        }

        // 构建更新请求，将新的 subReviews 列表保存回 DynamoDB
        Map<String, AttributeValue> key = new HashMap<>();
        key.put("donationId", AttributeValue.builder().s(donationId.toString()).build());

        UpdateItemRequest updateRequest = UpdateItemRequest.builder()
                .tableName(TABLE_NAME)
                .key(key)
                .updateExpression("SET subReviews = :updatedSubReviews")
                .expressionAttributeValues(Map.of(
                        ":updatedSubReviews", AttributeValue.builder()
                                .l(subReviews.stream()
                                        .map(this::convertSubReviewToAttributeValue)
                                        .collect(Collectors.toList()))
                                .build()
                ))
                .build();

        try {
            dynamoDBClient.updateItem(updateRequest);
            log.info("Successfully deleted SubReview with ID {} from DonationReview with ID {}", subReviewId, donationId);
        } catch (DynamoDbException e) {
            log.error("Failed to delete SubReview with ID {} from DonationReview with ID {}", subReviewId, donationId, e);
            throw new RuntimeException("Unable to delete SubReview", e);
        }
    }


    private DonationReview convertItemToDonationReview(Map<String, AttributeValue> item) {
        return DonationReview.builder()
                .donationId(item.containsKey("donationId") ? UUID.fromString(item.get("donationId").s()) : null)
                .reviewerSub(item.containsKey("reviewerSub") ? UUID.fromString(item.get("reviewerSub").s()) : null)
                .reviewContent(item.containsKey("reviewContent") ? item.get("reviewContent").s() : null)
                .reviewCreated(item.containsKey("reviewCreated") ? item.get("reviewCreated").s() : null)
                .reviewImageUrl(item.containsKey("reviewImageUrl") ? item.get("reviewImageUrl").s() : null)
                .subReviews(item.containsKey("subReviews") ? convertAttributeValueToSubReviewList(item.get("subReviews").l()) : null)
                .build();
    }
    private List<SubReview> convertAttributeValueToSubReviewList(List<AttributeValue> attributeValues) {
        List<SubReview> subReviews = new ArrayList<>();
        for (AttributeValue attributeValue : attributeValues) {
            Map<String, AttributeValue> subReviewMap = attributeValue.m();
            SubReview subReview = SubReview.builder()
                    .subReviewId(subReviewMap.containsKey("subReviewId") ? UUID.fromString(subReviewMap.get("subReviewId").s()) : null)
                    .content(subReviewMap.containsKey("content") ? subReviewMap.get("content").s() : null)
                    .authorSub(subReviewMap.containsKey("authorSub") ? UUID.fromString(subReviewMap.get("authorSub").s()) : null)
                    .timestamp(subReviewMap.containsKey("timestamp") ? subReviewMap.get("timestamp").s() : null)
                    .subReviewImageUrl(subReviewMap.containsKey("subReviewImageUrl") ? subReviewMap.get("subReviewImageUrl").s() : null)
                    .build();
            subReviews.add(subReview);
        }
        return subReviews;
    }
    private AttributeValue convertSubReviewToAttributeValue(SubReview subReview) {
        Map<String, AttributeValue> subReviewMap = new HashMap<>();
        subReviewMap.put("subReviewId", AttributeValue.builder().s(subReview.getSubReviewId().toString()).build());
        subReviewMap.put("content", AttributeValue.builder().s(subReview.getContent()).build());
        subReviewMap.put("authorSub", AttributeValue.builder().s(subReview.getAuthorSub().toString()).build());
        subReviewMap.put("timestamp", AttributeValue.builder().s(subReview.getTimestamp()).build());

        if (subReview.getSubReviewImageUrl() != null) {
            subReviewMap.put("subReviewImageUrl", AttributeValue.builder().s(subReview.getSubReviewImageUrl()).build());
        }

        return AttributeValue.builder().m(subReviewMap).build();
    }
}
