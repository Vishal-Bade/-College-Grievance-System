package com.college.grievancesystem.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "grievances")
public class Grievance {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String title;
    
    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;
    
    @Column(nullable = false)
    private String category; // ACADEMIC, INFRASTRUCTURE, HOSTEL, OTHER
    
    private String priority; // LOW, MEDIUM, HIGH, URGENT
    
    private String status; // PENDING, IN_PROGRESS, RESOLVED
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(name = "assigned_to")
    private Long assignedTo; // Staff ID (keeping simple for now, could be User object too)
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "incident_date")
    private java.time.LocalDate incidentDate;
    
    @Column(name = "resolution_days")
    private Integer resolutionDays;
    
    // Constructors
    public Grievance() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.status = "PENDING";
        this.priority = "MEDIUM";
    }
    
    public Grievance(String title, String description, String category, User user) {
        this.title = title;
        this.description = description;
        this.category = category;
        this.user = user;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.status = "PENDING";
        this.priority = "MEDIUM";
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getCategory() {
        return category;
    }
    
    public void setCategory(String category) {
        this.category = category;
    }
    
    public String getPriority() {
        return priority;
    }
    
    public void setPriority(String priority) {
        this.priority = priority;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
        this.updatedAt = LocalDateTime.now();
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    
    public Long getAssignedTo() {
        return assignedTo;
    }
    
    public void setAssignedTo(Long assignedTo) {
        this.assignedTo = assignedTo;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public java.time.LocalDate getIncidentDate() {
        return incidentDate;
    }
    
    public void setIncidentDate(java.time.LocalDate incidentDate) {
        this.incidentDate = incidentDate;
    }
    
    public Integer getResolutionDays() {
        return resolutionDays;
    }
    
    public void setResolutionDays(Integer resolutionDays) {
        this.resolutionDays = resolutionDays;
    }
    
    // Helper methods for UI
    public String getStatusBadge() {
        switch(status) {
            case "PENDING": return "warning";
            case "IN_PROGRESS": return "info";
            case "RESOLVED": return "success";
            default: return "secondary";
        }
    }
    
    public String getPriorityBadge() {
        switch(priority) {
            case "LOW": return "success";
            case "MEDIUM": return "info";
            case "HIGH": return "warning";
            case "URGENT": return "danger";
            default: return "secondary";
        }
    }
}