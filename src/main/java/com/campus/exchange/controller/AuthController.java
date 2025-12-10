package com.campus.exchange.controller;

import com.campus.exchange.dto.SignupRequest;
import com.campus.exchange.dto.VerifyOtpRequest;
import com.campus.exchange.model.User;
import com.campus.exchange.repository.PendingSignupRepositoryJson;
import com.campus.exchange.repository.UserRepositoryJson;
import com.campus.exchange.service.AuthService;
import com.campus.exchange.service.CustomLogger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController
{

    private final CustomLogger logger;
    private final AuthService authService;
    private final PendingSignupRepositoryJson pendingRepo;
    private final UserRepositoryJson userRepo;
    private final BCryptPasswordEncoder passwordEncoder;

    public AuthController(AuthService authService, PendingSignupRepositoryJson pendingRepo, UserRepositoryJson userRepo, BCryptPasswordEncoder passwordEncoder, CustomLogger logger)
    {

        this.authService = authService;
        this.pendingRepo = pendingRepo;
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.logger = logger;
    }

    /**Create a new signup request
     * Body- SignupRequest*/
    @PostMapping("/signup-request")
    public ResponseEntity<?> signup(@RequestBody SignupRequest request)
    {
        try
        {String message = authService.signupRequest(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(message);

        }
        catch (Exception e)
        {
            logger.log("AuthService", "Error in sending signup request");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestBody VerifyOtpRequest request)
    {
        try
        {
            // Add at the very start
            User user = authService.verifyOtp(request);
            logger.log("AuthService", "User verified and created: " + user.getUserId());
            return ResponseEntity.ok(user);
        }
        catch (Exception e)
        {
            logger.log("AuthService", "Error in verifying otp");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestParam String email, @RequestParam String password)
    {
        try
        {
            Map<String, String> sessionData = authService.login(email, password);
            return ResponseEntity.ok(sessionData);
        }
        catch (Exception e)
        {
            logger.log("AuthService", "Error in login");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Auth-Token") String token) {
        try {
            String message = authService.logout(token);
            return ResponseEntity.ok(message);
        } catch (Exception e) {
            logger.log("AuthService", "Error in logout");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

}