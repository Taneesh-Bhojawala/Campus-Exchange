// VerifyOtpRequest.java
package com.campus.exchange.dto;

import lombok.Data;

@Data
public class VerifyOtpRequest {
    private String email;
    private String otp;
}