package com.college.grievancesystem.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.college.grievancesystem.model.User;
import com.college.grievancesystem.service.UserService;
import com.college.grievancesystem.repository.UserRepository;

@Controller
public class UserController {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private UserRepository userRepository; // Direct repo access for finding all users (view only)
    
    // Show registration form
    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }
    
    // Handle registration form submission
    @PostMapping("/register")
    public String registerUser(@ModelAttribute User user, Model model) {
        try {
            // 1. Basic Validation
            if (user.getEmail() == null || user.getEmail().isEmpty()) {
                model.addAttribute("error", "Email is required!");
                return "register";
            }
            if (user.getPassword() == null || user.getPassword().length() < 6) {
                model.addAttribute("error", "Password must be at least 6 characters!");
                return "register";
            }

            // 2. Duplicate Email Check
            if (userService.isEmailTaken(user.getEmail())) {
                model.addAttribute("error", "Email already registered! Please login.");
                return "register";
            }
            
            // 3. Save user (UserService handles hashing)
            userService.registerUser(user);
            
            // 4. Show success message (Pass object to success view)
            model.addAttribute("message", "Registration successful! You can now login.");
            model.addAttribute("user", user);
            
            return "register-success";
            
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Registration failed due to system error: " + e.getMessage());
            return "register";
        }
    }
    
    // Show all users (for testing/admin)
    @GetMapping("/users")
    public String showUsers(Model model) {
        List<User> userList = userRepository.findAll();
        model.addAttribute("users", userList);
        return "users-list";
    }
}