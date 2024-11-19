package us.reindeers.giftservice.domain.entity;

public enum GiftRequestStatus {

    /*
    start status
    can be cancelled
    can be edited
     */
    PENDING,
    /*
    part of the gift has been processed or donated
    cannot be cancelled
    can be edited, but quantity cannot be less than in_progress_quantity
     */
    IN_PROGRESS,
    /*
    all the quantity has been processed or donated
    cannot be cancelled
    cannot be edited
     */
    FULFILLED_IN_PROGRESS,
    /*
    request has been fulfilled
    cannot be cancelled
    cannot be edited
     */
    COMPLETED,
    /*
    request was cancelled
    cannot be edited
    view only
     */
    CANCELLED
}
