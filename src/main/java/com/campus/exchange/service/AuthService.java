package com.campus.exchange.service;

import com.campus.exchange.dto.SignupRequest;
import com.campus.exchange.dto.VerifyOtpRequest;
import com.campus.exchange.model.PendingSignup;
import com.campus.exchange.model.Sessions;
import com.campus.exchange.model.User;
import com.campus.exchange.repository.PendingSignupRepositoryJson;
import com.campus.exchange.repository.SessionRepositoryJson;
import com.campus.exchange.repository.UserRepositoryJson;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.*;
import java.util.*;
import java.util.Optional;
import java.util.UUID;

@Service
public class AuthService
{
    private final JavaMailSender javaMailSender;
    private final PendingSignupRepositoryJson pendingRepo;
    private final UserRepositoryJson userRepo;
    private final BCryptPasswordEncoder passwordEncoder;
    private final SessionRepositoryJson sessionRepo;

    public AuthService(JavaMailSender javaMailSender, PendingSignupRepositoryJson pendingRepo, UserRepositoryJson userRepo, BCryptPasswordEncoder passwordEncoder, SessionRepositoryJson sessionRepo)
    {
        this.javaMailSender = javaMailSender;
        this.pendingRepo = pendingRepo;
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.sessionRepo = sessionRepo;
    }

    private String generateOtp()
    {
        return String.format("%06d", (int)(Math.random() * 900000) + 100000);
    }

    public String signupRequest(SignupRequest signupRequest) throws Exception
    {
        Optional<User> user = userRepo.findByEmail(signupRequest.getEmail());
        if(user.isPresent())
        {
            throw new Exception("User already exists");
        }

        if(pendingRepo.findByEmail(signupRequest.getEmail()).isPresent())
        {
            throw new Exception("OTP already sent. Please verify your email");
        }

        String hashedPassword = passwordEncoder.encode(signupRequest.getPassword());

        String otp = generateOtp();
        long expiresAt = System.currentTimeMillis() + (2*60*1000);

        PendingSignup pendingSignup = new PendingSignup(signupRequest.getEmail(), signupRequest.getName(), hashedPassword, signupRequest.getHostelNumber(), signupRequest.getGender(), signupRequest.getCollege(), otp, expiresAt);
        pendingRepo.save(pendingSignup);

        System.out.println("OTP for " + signupRequest.getEmail() + ": " + otp);
        SimpleMailMessage message = getSimpleMailMessage(signupRequest, otp);
        javaMailSender.send(message);
        return "OTP sent successfully, please verify";
    }

    private static SimpleMailMessage getSimpleMailMessage(SignupRequest signupRequest, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("campus.exchange.iiitb@gmail.com");
        message.setTo(signupRequest.getEmail());
        message.setSubject("One Time Password (OTP) for your account on OrangeCat");
        message.setText("Hi, " + signupRequest.getName() + "\nUse " + otp + " as One Time Password (OTP) to verify your account with OrangeCat. This OTP is valid for 2 minutes.\nPlease do not share this OTP with anyone for security reasons.\nRegards,\nTeam OrangeCat 🐈\n");
        return message;
    }

    public User verifyOtp(VerifyOtpRequest verifyOtpDto) throws Exception
    {
        Optional<PendingSignup> pendingSignup = pendingRepo.findByEmail(verifyOtpDto.getEmail());

        if(pendingSignup.isEmpty())
        {
            throw new Exception("No pending signup found!");
        }

        PendingSignup pending = pendingSignup.get();

        if(!pending.getOtp().equals(verifyOtpDto.getOtp()))
        {
            throw new Exception("Invalid OTP");
        }

        User user = new User(UUID.randomUUID().toString(), pending.getName(), pending.getEmail(), pending.getHashPassword(), pending.getHostelNumber(), pending.getGender(), pending.getCollege(), true);
        userRepo.save(user);
        pendingRepo.deleteByEmail(verifyOtpDto.getEmail());

        return user;
    }

    public Map<String, String> login(String email, String password) throws Exception {

        Optional<User> userOpt = userRepo.findByEmail(email);
        if (userOpt.isEmpty())
            throw new Exception("User does not exist");

        User user = userOpt.get();

        if (!passwordEncoder.matches(password, user.getPassword()))
            throw new Exception("Invalid password");

        if (!user.isEnabled())
            throw new Exception("User not verified");

        // Create session token
        String token = UUID.randomUUID().toString();
        long expiresAt = System.currentTimeMillis() + 24 * 60 * 60 * 1000; // 24 hours

        Sessions session = new Sessions(token, user.getUserId(), expiresAt);
        sessionRepo.save(session);

        Map<String, String> response = new HashMap<>();
        response.put("token", token);
        response.put("userId", user.getUserId());

        return response;
    }

    public String verifySession(String token) throws Exception
    {

        Optional<Sessions> sessionOptional = sessionRepo.findByToken(token);

        if (sessionOptional.isEmpty())
            throw new Exception("Invalid or missing token");

        Sessions session = sessionOptional.get();

        if (session.getExpiresAt() < System.currentTimeMillis()) {
            sessionRepo.deleteToken(token);
            throw new Exception("Session expired. Please login again.");
        }

        return session.getUserId();  // return logged-in user
    }

}
