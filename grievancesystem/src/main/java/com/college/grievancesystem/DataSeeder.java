package com.college.grievancesystem;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.college.grievancesystem.model.User;
import com.college.grievancesystem.repository.UserRepository;
import com.college.grievancesystem.service.UserService;

@Component
public class DataSeeder implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private UserService userService;

    @Override
    public void run(String... args) throws Exception {
        // Admin
        if (userRepository.findByEmail("admin@college.edu") == null) {
            User admin = new User();
            admin.setEmail("admin@college.edu");
            admin.setFullName("System Admin");
            admin.setRole("ADMIN");
            admin.setCreatedAt(LocalDateTime.now());
            admin.setPassword("admin123");
            userService.registerUser(admin);
            System.out.println("Seeded test admin account.");
        }

        // Student
        if (userRepository.findByEmail("student@college.edu") == null) {
            User student = new User();
            student.setEmail("student@college.edu");
            student.setFullName("Demo Student");
            student.setRole("STUDENT");
            student.setCreatedAt(LocalDateTime.now());
            student.setPassword("student123");
            userService.registerUser(student);
            System.out.println("Seeded test student account.");
        }

        // Staff
        if (userRepository.findByEmail("staff@college.edu") == null) {
            User staff = new User();
            staff.setEmail("staff@college.edu");
            staff.setFullName("Demo Staff");
            staff.setRole("STAFF");
            staff.setCreatedAt(LocalDateTime.now());
            staff.setPassword("staff123");
            userService.registerUser(staff);
            System.out.println("Seeded test staff account.");
        }
    }
}
