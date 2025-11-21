package com.campus.exchange.model;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BlockEntry {
    private String itemID;
    private String userID;
    private long blockedUntil;
}
