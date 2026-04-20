package com.college.grievancesystem.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.college.grievancesystem.model.Grievance;
import com.college.grievancesystem.model.User;
import com.college.grievancesystem.repository.UserRepository;
import com.college.grievancesystem.service.GrievanceService;
import com.college.grievancesystem.service.UserService;

import jakarta.servlet.http.HttpSession;

@Controller
public class HomeController {

    @Autowired
    private UserService userService;
    
    @Autowired
    private GrievanceService grievanceService;

    @Autowired
    private UserRepository userRepository; // For admin user list

    // ========== HELPER METHODS ==========
    private boolean isAuthenticated(HttpSession session) {
        return session.getAttribute("userEmail") != null;
    }

    private boolean hasRole(HttpSession session, String role) {
        String userRole = (String) session.getAttribute("userRole");
        return isAuthenticated(session) && role.equals(userRole);
    }

    // ========== AUTHENTICATION ==========
    @GetMapping("/")
    public String homePage() {
        return "index"; // Maps to index.html
    }

    @GetMapping("/login")
    public String showLoginForm(@RequestParam(value = "error", required = false) String error,
            @RequestParam(value = "logout", required = false) String logout,
            Model model) {
        if (error != null) {
            model.addAttribute("error", "Invalid email or password!");
        }
        if (logout != null) {
            model.addAttribute("message", "You have been logged out successfully!");
        }
        return "login";
    }

    @PostMapping("/login")
    public String handleLogin(@RequestParam("email") String email,
            @RequestParam("password") String password,
            HttpSession session,
            Model model) {
        System.out.println("Login attempt for: " + email);
        try {
            User user = userService.authenticate(email, password);

            if (user != null) {
                System.out.println("Login successful. Role: " + user.getRole());
                // Set Session Attributes
                session.setAttribute("userEmail", user.getEmail());
                session.setAttribute("userRole", user.getRole());
                session.setAttribute("userId", user.getId());
                session.setAttribute("userName", user.getFullName());
                
                return "redirect:/dashboard";
            } else {
                System.out.println("Login failed: Invalid credentials");
                model.addAttribute("error", "Invalid Credentials! Check email or password.");
                return "login";
            }
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Login error: " + e.getMessage());
            return "login";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login?logout=true";
    }

    // ========== DASHBOARD ROUTING ==========
    @GetMapping("/dashboard")
    public String showDashboard(HttpSession session) {
        if (!isAuthenticated(session)) return "redirect:/login";

        String role = (String) session.getAttribute("userRole");
        if (role == null) return "redirect:/login";

        switch (role) {
            case "STUDENT": return "redirect:/student/dashboard";
            case "STAFF":   return "redirect:/staff/dashboard";
            case "ADMIN":   return "redirect:/admin/dashboard";
            default:        return "redirect:/";
        }
    }

    // ========== ROLE DASHBOARDS ==========
    @GetMapping("/student/dashboard")
    public String studentDashboard(HttpSession session, Model model) {
        if (!hasRole(session, "STUDENT")) return "redirect:/dashboard";

        try {
            String email = (String) session.getAttribute("userEmail");
            User user = userService.findByEmail(email);
            
            if (user == null) {
                session.invalidate();
                return "redirect:/login?error=UserNotFound";
            }
            
            List<Grievance> myGrievances = grievanceService.getGrievancesByUser(user);
            
            long pendingCount = myGrievances.stream().filter(g -> "PENDING".equals(g.getStatus())).count();
            long resolvedCount = myGrievances.stream().filter(g -> "RESOLVED".equals(g.getStatus())).count();

            model.addAttribute("user", user);
            model.addAttribute("email", email);
            model.addAttribute("totalCount", myGrievances.size());
            model.addAttribute("pendingCount", pendingCount);
            model.addAttribute("resolvedCount", resolvedCount);
            model.addAttribute("grievances", myGrievances); 

            return "student-dashboard";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Dashboard error: " + e.getMessage());
            return "error"; // You might need a simple error.html or redirect
        }
    }

    @GetMapping("/staff/dashboard")
    public String staffDashboard(HttpSession session, Model model) {
        if (!hasRole(session, "STAFF")) return "redirect:/dashboard";

        try {
            String email = (String) session.getAttribute("userEmail");
            User staffUser = userService.findByEmail(email);

            if (staffUser == null) {
                session.invalidate();
                return "redirect:/login?error=UserNotFound";
            }

            List<Grievance> assignedGrievances = grievanceService.getGrievancesAssignedTo(staffUser.getId());
            List<Grievance> pendingGrievances = grievanceService.getGrievancesByStatus("PENDING");

            model.addAttribute("email", email);
            model.addAttribute("assignedGrievances", assignedGrievances);
            model.addAttribute("pendingGrievances", pendingGrievances);
            model.addAttribute("assignedCount", assignedGrievances.size());

            return "staff-dashboard";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Dashboard error: " + e.getMessage());
            return "error";
        }
    }

    @GetMapping("/admin/dashboard")
    public String adminDashboard(HttpSession session, Model model) {
        if (!hasRole(session, "ADMIN")) return "redirect:/dashboard";

        try {
            String email = (String) session.getAttribute("userEmail");
            List<Grievance> allGrievances = grievanceService.getAllGrievances();
            
            long pendingCount = allGrievances.stream().filter(g -> "PENDING".equals(g.getStatus())).count();
            long inProgressCount = allGrievances.stream().filter(g -> "IN_PROGRESS".equals(g.getStatus())).count();
            long resolvedCount = allGrievances.stream().filter(g -> "RESOLVED".equals(g.getStatus())).count();

            model.addAttribute("email", email);
            model.addAttribute("totalGrievances", allGrievances.size());
            model.addAttribute("pendingGrievances", pendingCount);
            model.addAttribute("inProgressGrievances", inProgressCount);
            model.addAttribute("resolvedGrievances", resolvedCount);
            model.addAttribute("allGrievances", allGrievances);
            model.addAttribute("userList", userRepository.findAll());

            return "admin-dashboard";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Dashboard error: " + e.getMessage());
            return "error";
        }
    }

    // ========== GRIEVANCE MANAGEMENT ==========
    @GetMapping("/grievance/new")
    public String showGrievanceForm(HttpSession session, Model model) {
        if (!isAuthenticated(session)) return "redirect:/login";
        model.addAttribute("grievance", new Grievance());
        return "grievance-form";
    }
    
    @PostMapping("/grievance/submit")
    public String submitGrievance(@RequestParam("title") String title,
                                  @RequestParam("description") String description,
                                  @RequestParam("category") String category,
                                  @RequestParam("priority") String priority,
                                  @RequestParam("incidentDate") java.time.LocalDate incidentDate,
                                  HttpSession session,
                                  Model model) {
        if (!isAuthenticated(session)) return "redirect:/login";

        String email = (String) session.getAttribute("userEmail");
        User user = userService.findByEmail(email);
        
        try {
            Grievance grievance = new Grievance();
            grievance.setTitle(title);
            grievance.setDescription(description);
            grievance.setCategory(category);
            grievance.setPriority(priority);
            grievance.setIncidentDate(incidentDate);
            grievance.setUser(user);
            grievance.setStatus("PENDING");
            // IDs are auto-generated
            
            grievanceService.saveGrievance(grievance);
            
            model.addAttribute("message", "Grievance submitted successfully!");
            model.addAttribute("grievance", grievance);
            return "grievance-success";

        } catch (Exception e) {
            model.addAttribute("error", "Error creating grievance: " + e.getMessage());
            return "grievance-form";
        }
    }

    @GetMapping("/grievance/my")
    public String viewMyGrievances(HttpSession session, Model model) {
        if (!isAuthenticated(session)) return "redirect:/login";

        String email = (String) session.getAttribute("userEmail");
        User user = userService.findByEmail(email);
        
        List<Grievance> myGrievances = grievanceService.getGrievancesByUser(user);
        model.addAttribute("grievances", myGrievances);
        
        return "my-grievances";
    }

    @GetMapping("/grievance/all")
    public String viewAllGrievances(HttpSession session, Model model) {
        if (!hasRole(session, "ADMIN")) return "redirect:/dashboard";

        model.addAttribute("grievances", grievanceService.getAllGrievances());
        return "all-grievances";
    }

    @GetMapping("/grievance/{id}")
    public String viewGrievance(@PathVariable("id") Long id, HttpSession session, Model model) {
        if (!isAuthenticated(session)) return "redirect:/login";

        Grievance grievance = grievanceService.getGrievanceById(id);
        
        if (grievance == null) {
            return "redirect:/dashboard"; // Handle not found
        }
        
        // Security check: Student can only view OWN grievance
        String role = (String) session.getAttribute("userRole");
        String email = (String) session.getAttribute("userEmail");
        
        if ("STUDENT".equals(role) && !grievance.getUser().getEmail().equals(email)) {
             return "redirect:/dashboard"; // Unauthorized access to another's grievance
        }

        model.addAttribute("grievance", grievance);
        return "grievance-detail";
    }

    // ========== UPDATES & ASSIGNMENT ==========
    
    // Update status (Admin/Staff)
    @PostMapping("/grievance/update-status")
    public String updateStatus(@RequestParam("id") Long id,
                               @RequestParam("status") String status,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {
        
        if (!isAuthenticated(session)) return "redirect:/login";
        
        String role = (String) session.getAttribute("userRole");
        if (!"ADMIN".equals(role) && !"STAFF".equals(role)) {
            return "redirect:/dashboard";
        }

        Grievance g = grievanceService.getGrievanceById(id);
        if (g != null) {
            g.setStatus(status);
            
            // If Staff updates it, ensure they are assigned? Optional logic.
            // For now just update status.
            
            grievanceService.saveGrievance(g);
        }
        
        return "redirect:/grievance/" + id;
    }

    // Assign to Staff (Admin only)
    @PostMapping("/grievance/assign")
    public String assignGrievance(@RequestParam("grievanceId") Long grievanceId,
                                  @RequestParam(value = "assignedTo", defaultValue = "0") Long assignedTo,
                                  @RequestParam(value = "resolutionDays", defaultValue = "7") Integer resolutionDays,
                                  HttpSession session) {
        
        if (!hasRole(session, "ADMIN")) return "redirect:/dashboard";

        Grievance g = grievanceService.getGrievanceById(grievanceId);
        // Note: In a real app we would check if assignedTo user exists and is STAFF.
        // Here we just set the ID as per current simplified requirements.
        
        if (g != null && assignedTo > 0) {
            g.setAssignedTo(assignedTo);
            g.setResolutionDays(resolutionDays);
            if ("PENDING".equals(g.getStatus())) {
                g.setStatus("IN_PROGRESS");
            }
            grievanceService.saveGrievance(g);
        }

        return "redirect:/admin/dashboard";
    }

    // Quick Update from Staff/Admin Dashboards
    @PostMapping("/grievance/update")
    public String quickUpdate(@RequestParam("id") Long id,
                              @RequestParam("status") String status,
                              HttpSession session) {
        
        if (!isAuthenticated(session)) return "redirect:/login";
        
        // Similar to update-status but redirects back to dashboard usually
        Grievance g = grievanceService.getGrievanceById(id);
        if (g != null) {
            g.setStatus(status);
            grievanceService.saveGrievance(g);
        }
        
        String role = (String) session.getAttribute("userRole");
        if ("ADMIN".equals(role)) return "redirect:/admin/dashboard";
        if ("STAFF".equals(role)) return "redirect:/staff/dashboard";
        
        return "redirect:/dashboard";
    }
}
