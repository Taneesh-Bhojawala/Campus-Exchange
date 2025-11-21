// SignupRequest.java
package com.campus.exchange.dto;

import lombok.Data;

@Data
public class SignupRequest {
    private String name;
    private String email;
    private String password;
    private String hostelNumber;
    private String gender;
    private String college;
}
