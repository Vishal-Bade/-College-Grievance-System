package com.college.grievancesystem.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.college.grievancesystem.model.Grievance;
import com.college.grievancesystem.model.User;

@Repository
public interface GrievanceRepository extends JpaRepository<Grievance, Long> {
    List<Grievance> findByUser(User user);
    List<Grievance> findByStatus(String status);
    List<Grievance> findByAssignedTo(Long assignedTo);
}
