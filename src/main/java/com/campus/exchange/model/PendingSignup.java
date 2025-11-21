package com.campus.exchange.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor              //lombok annotation that creates the default constructor
@AllArgsConstructor             //lombok annotation that creates the all parameter constructor.
public class PendingSignup
{
    private String mail;        //email of the user

    private String name;        //real name of the user
    private String hashPassword;//hashed password for login
    private String hostelNumber;//hostel number of the user
    private String gender;      //gender of the user
    private String college;     //college of the user

    private String otp;         //otp that is generated for the signup
    private long expiresAt;     //the time when otp will expire (now + 5 minutes)
}
