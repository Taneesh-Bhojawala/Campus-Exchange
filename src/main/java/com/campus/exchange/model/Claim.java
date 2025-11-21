package com.campus.exchange.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Claim {
    private String claimID;
    private String itemID;
    private String claimerID;
    private String listerID;
    private String status;
    private long createdAt;
}
