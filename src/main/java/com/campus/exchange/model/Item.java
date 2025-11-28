package com.campus.exchange.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Item {
    private String itemId;      //unique(UUID) id of the  item
    private String listerId;    //unique(UUID) id of the lister
    private String college;     // college name of the lister

    private int quantity;       //quantity of the item
    private String title;       //title/name of the item
    private String description; //description of the item

    private String category;    // category in which item belongs(electronic, books, general..)
    private double price;       // price of the item

    private String imagePath;   // path to the image of the item uploaded
    private String status;      //CLAIMED,LISTED,PENDING(in process)
    private long createdAt;     // Epoch millis at creation time
}
