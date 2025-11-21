package com.campus.exchange.model;

public class Notification {
    private String id;      //unique id of the notification
    private String UserId;  // id of the recipient
    private String type;    // e.g. CLAIM_CREATED, CLAIM_ACCEPTED, CLAIM_REJECTED, ITEM_EXPIRED
    private String title;   // short title for UI
    private String message; // long message may include(email/contact)
    private long timestamp; // epoch millis
    private boolean read;   //false by default
}
