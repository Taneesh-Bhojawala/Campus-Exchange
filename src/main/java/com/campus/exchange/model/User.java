package com.campus.exchange.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class User {

    /**
    Unique identifier of the user
    Generated using UUID.randomUUID().toString() during otp verification
     */
    private String userId;

    /**
    User's real name
     */
    private String name;

    /**
     * Email address of the user.
     * This is unique:
     *  - Used in login
     *  - Used to check if user already exists
     *  - Used to show lister email to accepted claimer, and vice-versa
     */
    private String email;

    /**
     * BCrypt-hashed password.
     * We NEVER store raw password.
     * Password is hashed first in AuthService during signup request.
     * hashedPassword from PendingSignup becomes User.password after OTP verification.
     */
    private String password;

    /**
     * Hostel number chosen during signup.
     */
    private String hostelNumber;

    /**
     * Gender of the user (male/female).
     * Used only for profile completeness.
     */
    private String gender;

    /**
     * Selected during signup
     * Must match entry in database (json)
     */
    private String college;

    /**
     * True if the account is active.
     *
     * In the project:
     * Always set to true after OTP verification.
     * If OTP not verified, user is NOT created, so this field ensures no unverified accounts exist.
     */
    private boolean enabled;

//    /**
//     * How the user authenticated.
//     *
//     * For project:
//     * "LOCAL" (password + OTP)
//     * Google authentication can be added in future
//     */
//    private String authProvider;
}
