package com.campus.exchange.controller;

import com.campus.exchange.dto.SignupRequest;
import com.campus.exchange.dto.VerifyOtpRequest;
import com.campus.exchange.model.PendingSignup;
import com.campus.exchange.model.User;
import com.campus.exchange.repository.PendingSignupRepositoryJson;
import com.campus.exchange.repository.UserRepositoryJson;
import com.campus.exchange.service.AuthService;
import org.springframework.boot.SpringApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final PendingSignupRepositoryJson pendingRepo;
    private final UserRepositoryJson userRepo;
    private final BCryptPasswordEncoder passwordEncoder;

    public AuthController(AuthService authService,
                          PendingSignupRepositoryJson pendingRepo,
                          UserRepositoryJson userRepo,
                          BCryptPasswordEncoder passwordEncoder) {

        this.authService = authService;
        this.pendingRepo = pendingRepo;
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignupRequest request) {
        try {
            String message = authService.signupRequest(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(message);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestBody VerifyOtpRequest request) {
        try {
            User user = authService.verifyOtp(request);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/resend-otp")
    public ResponseEntity<?> resendOtp(@RequestParam String email) {
        try {
            Optional<PendingSignup> pending = pendingRepo.findByEmail(email);

            if (pending.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("No pending signup exists for this email.");
            }

            String newOtp = String.format("%06d", (int) (Math.random() * 900000) + 100000);
            long expiresAt = System.currentTimeMillis() + (2 * 60 * 1000);
            PendingSignup signup = pending.get();
            signup.setOtp(newOtp);
            signup.setExpiresAt(expiresAt);

            pendingRepo.save(signup);

            System.out.println("NEW OTP for " + email + " = " + newOtp);

            return ResponseEntity.ok("New OTP sent (console).");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error while resending OTP.");
        }
    }
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestParam String email,
                                   @RequestParam String password) {
        try {
            Optional<User> userOpt = userRepo.findByEmail(email);

            if (userOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("User does not exist.");
            }

            User user = userOpt.get();

            // Check password — here using your real hashed password field
            boolean matches = passwordEncoder.matches(password, user.getPassword());

            if (!matches) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Invalid password.");
            }

            if (!user.isEnabled()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("User not verified.");
            }

            return ResponseEntity.ok(user);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Login failed.");
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> me() {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
                .body("Token authentication not implemented.");
    }
}
