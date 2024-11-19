package us.reindeers.giftservice.domain.entity;

public enum GiftDonationStatus {
    /*
    start status
    can be cancelled
    can be edited
    require recipient confirm
     */
    PENDING,
    /*
    after recipient confirm, change to PROCESSING
    can be cancelled by recipient or donor (require the other one confirm)
    cannot be edited
     */
    PROCESSING,
    /*
    after providing tracking number, change to SHIPPING
    can be cancelled
    cannot be edited
     */
    SHIPPING,
    /*
    receiver can change status to RECEIVED after receiving the gift
    cannot be cancelled
    cannot be edited
     */
    RECEIVED,
    /*
    cannot be edited
    view only
     */
    //if donation status processing, need both sides' approval to change to CANCELLED status
    CANCELLED,

    RECEIVER_CANCELLED,
    
    DONOR_CANCELLED


}
