package com.college.grievancesystem.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.college.grievancesystem.model.User;
import com.college.grievancesystem.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    // Register a new user with hashed password
    public void registerUser(User user) {
        // Hash the password before saving
        String hashedPassword = hashPassword(user.getPassword());
        user.setPassword(hashedPassword);
        
        // Set default values if missing
        if (user.getRole() == null || user.getRole().isEmpty()) {
            user.setRole("STUDENT"); // Default role
        }
        if (user.getCreatedAt() == null) {
            user.setCreatedAt(LocalDateTime.now());
        }
        
        userRepository.save(user);
    }

    // Authenticate user by comparing hashed passwords (with plaintext fallback)
    public User authenticate(String email, String password) {
        User user = userRepository.findByEmail(email);
        
        if (user != null) {
            String inputHashed = hashPassword(password);
            String storedPassword = user.getPassword();
            
            // Check hashed match
            if (inputHashed.equals(storedPassword)) {
                return user;
            }
            
            // Fallback: Check plaintext match (for pre-seeded test users)
            if (password.equals(storedPassword)) {
                // Optional: Upgrade password to hash here if we wanted to be helpful
                // user.setPassword(inputHashed);
                // userRepository.save(user);
                return user;
            }
        }
        return null;
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    // Check if email already exists
    public boolean isEmailTaken(String email) {
        return userRepository.findByEmail(email) != null;
    }

    // SHA-256 Password Hashing
    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedhash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(encodedhash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }

    private String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (int i = 0; i < hash.length; i++) {
            String hex = Integer.toHexString(0xff & hash[i]);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
