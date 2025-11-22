package com.campus.exchange.model;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BlockEntry {
    private String itemID;//stores the id of the item which is blocked
    private String userID;//stores the id of user
    private long blockedUntil;//stores until what time do we need to keep a particular user blocked for that object.
}
