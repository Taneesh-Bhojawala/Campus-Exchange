package com.campus.exchange.service;

import com.campus.exchange.dto.SignupRequest;
import com.campus.exchange.dto.VerifyOtpRequest;
import com.campus.exchange.model.PendingSignup;
import com.campus.exchange.model.User;
import com.campus.exchange.repository.PendingSignupRepositoryJson;
import com.campus.exchange.repository.UserRepositoryJson;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class AuthService
{
    private final PendingSignupRepositoryJson pendingRepo;
    private final UserRepositoryJson userRepo;
    private final BCryptPasswordEncoder passwordEncoder;

    public AuthService(PendingSignupRepositoryJson pendingRepo, UserRepositoryJson userRepo, BCryptPasswordEncoder passwordEncoder)
    {
        this.pendingRepo = pendingRepo;
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
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

        return "OTP sent successfully, please verify";
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

//    public User login()
//    {
//
//    }
}
