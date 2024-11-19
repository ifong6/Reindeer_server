package us.reindeers.common.response.constant.template;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ReturnCode {

    /**操作成功**/
    RC100(100,"success"),

    /**服务错误**/
    RC400(400, "service failed"),

    /**服务异常**/
    RC500(500,"system error, try again later"),

    USER_NOT_EXIST(1037,"user not existed"),
    ROLE_NOT_SET(1038,"user role not set"),
    ROLE_NOT_EXIST(1039,"role not exist"),
    DATABASE_ACCESS_ERROR(1040, "Database access error"),
    INVALID_INPUT(1041,"invalid input"),
    ID_OBTAINED_FAILURE(1042, "Failed to obtain ID after retries"),
    ADDRESS_NOT_SUBMITTED(1043,"Address not submitted"),
    ERROR_UPLOADING_AVATAR(1044, "Error uploading avatar"),
    INVALID_USER(1045,"Invalid user"),
    EMAIL_ALREADY_EXISTS(1046,"Email already existed."),


    DONOR_NOT_EXIST(1150,"donor not exist"),
    GIFT_NOT_EXIST(1151, "gift not exist"),
    INVALID_DONATION_STATUS(1152, "donation status not in right status"),
    REQUEST_NOT_EXIST(1153,"request not exist"),
    INVALID_REQUEST_STATUS(1154, "request status not in right status"),
    REQUEST_IS_FULFILLED(1155,"request quantity is fulfilled"),

    NOTIFICATION_NOT_FOUND(1200, "notification not found"),

    REVIEW_NOT_FOUND(1210, "review not found"),
    SUB_REVIEW_NOT_FOUND(1211, "subreview not found");




    /**自定义状态码**/
    private final int code;
    /**自定义描述**/
    private final String message;


}
