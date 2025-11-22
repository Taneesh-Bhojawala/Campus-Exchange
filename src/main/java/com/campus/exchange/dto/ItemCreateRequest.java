//ItemCreateRequest.java
package com.campus.exchange.dto;

import lombok.Data;

@Data
public class ItemCreateRequest {
    private String listerUserId;
    private int quantity;
    private String title;
    private String description;
    private String category;
    private double price;
    private String imagePath;
    private String status;
    private long createdAt;
}
