package com.campus.exchange.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Claim {
    private String claimID;//assign id to a claim which was requested
    private String itemID; //stores the id of the item to be claimed
    private String claimerID;//stores the id of claimer
    private String listerID;//stores the id of person listing the item
    private String status;//stores the state of object i.e. ACCEPTED, WAITING, REJECTED
    private long createdAt;//stores at what time is the object claim created
}
