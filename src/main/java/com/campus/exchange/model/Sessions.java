package com.campus.exchange.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Sessions {
    private String token;
    private String userId;
    private long expiresAt;
}
