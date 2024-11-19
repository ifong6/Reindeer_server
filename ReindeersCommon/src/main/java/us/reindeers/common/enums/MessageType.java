package us.reindeers.common.enums;

public enum MessageType {

    SYSTEM_MESSAGE,
    WELCOME_MESSAGE,
    //gift request 发布
    REQUEST_PUBLISHED,
    //donation 创建
    DONATION_RECEIVED,
    //donation approve
    DONATION_APPROVED,
    DONATION_CANCELLED,
    DONATION_CANCELLED_NEEDS_APPROVAL,
    DONATION_SHIPPED,
    DONATION_COMPLETED
}
