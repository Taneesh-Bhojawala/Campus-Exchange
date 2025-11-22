package com.campus.exchange.controller;

import com.campus.exchange.dto.SignupRequest;
import com.campus.exchange.dto.VerifyOtpRequest;
import com.campus.exchange.model.User;
import com.campus.exchange.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController
{
    private AuthService authService;
    public AuthController(AuthService authService)
    {
        this.authService = authService;
    }

    @PostMapping("/signup-request")
    public ResponseEntity<?> signupRequest(@RequestBody SignupRequest signupRequest)
    {
        try
        {
            String result = authService.signupRequest(signupRequest);
            return ResponseEntity.ok().body(result);
        }
        catch (Exception e)
        {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    public ResponseEntity<?> verifyOtp(@RequestBody VerifyOtpRequest verifyOtpRequest)
    {
        try
        {
            User user = authService.verifyOtp(verifyOtpRequest);
            return ResponseEntity.ok().body(user);
        }
        catch (Exception e)
        {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
