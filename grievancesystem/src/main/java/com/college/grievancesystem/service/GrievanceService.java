package com.college.grievancesystem.service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.college.grievancesystem.model.Grievance;
import com.college.grievancesystem.model.User;
import com.college.grievancesystem.repository.GrievanceRepository;

@Service
public class GrievanceService {

    @Autowired
    private GrievanceRepository grievanceRepository;

    public void saveGrievance(Grievance grievance) {
        if (grievance.getCreatedAt() == null) {
            grievance.setCreatedAt(LocalDateTime.now());
        }
        grievance.setUpdatedAt(LocalDateTime.now());
        grievanceRepository.save(grievance);
    }

    public List<Grievance> getAllGrievances() {
        List<Grievance> list = grievanceRepository.findAll();
        return list != null ? list : Collections.emptyList();
    }

    public List<Grievance> getGrievancesByUser(User user) {
        if (user == null) return Collections.emptyList();
        
        List<Grievance> list = grievanceRepository.findByUser(user);
        return list != null ? list : Collections.emptyList();
    }

    public Grievance getGrievanceById(Long id) {
        if (id == null) return null;
        Optional<Grievance> grievance = grievanceRepository.findById(id);
        return grievance.orElse(null);
    }
    
    public List<Grievance> getGrievancesByStatus(String status) {
        if (status == null) return Collections.emptyList();
        List<Grievance> list = grievanceRepository.findByStatus(status);
        return list != null ? list : Collections.emptyList();
    }
    
    public List<Grievance> getGrievancesAssignedTo(Long staffId) {
        if (staffId == null) return Collections.emptyList();
        List<Grievance> list = grievanceRepository.findByAssignedTo(staffId);
        return list != null ? list : Collections.emptyList();
    }
}
