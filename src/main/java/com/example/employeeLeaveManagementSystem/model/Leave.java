package com.example.employeeLeaveManagementSystem.model;

import java.sql.Date;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

@Entity
@Table(name = "leaves")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Leave {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", referencedColumnName = "id", nullable = false)
    private Employee employee; 

    @Enumerated(EnumType.STRING)
    private LeaveType leaveType; 

    private Date fromDate; 

    private Date toDate; 
    
    private int numberOfDays;

    private String reason; 

    @Enumerated(EnumType.STRING)
    private LeaveStatus status;
    
    @PrePersist
    @PreUpdate
    public void calculateNumberOfDays() {
    	LocalDate startDate = fromDate.toLocalDate();
        LocalDate endDate = toDate.toLocalDate();
        
        numberOfDays = (int) ChronoUnit.DAYS.between(startDate, endDate) + 1;
    }
    
}