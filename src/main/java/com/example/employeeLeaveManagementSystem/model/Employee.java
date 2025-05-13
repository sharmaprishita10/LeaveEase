package com.example.employeeLeaveManagementSystem.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
public class Employee implements Serializable{

	
	private static final long serialVersionUID = 1L; // Explicit serialVersionUID
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;  

    @Column(unique = true)
    private String empId;  

    private String name;  

    @Column(unique = true)
    @Pattern(regexp = "^[0-9]{10}$", message = "Mobile number must be 10 digits")  // Validation for mobile number (10 digits)
    private String mobileNumber;  

    @Email(message = "Invalid email address")
    @Column(unique = true)
    private String email; 

    private String password;  

    @Temporal(TemporalType.DATE)
    private Date dob;

    @ManyToOne(optional = true)
    @JoinColumn(name = "manager")
    private Employee manager;  

    private String address;  

    @Lob
    private byte[] profilePic;  

    @Pattern(regexp = "^[0-9]{12}$", message = "Aadhaar number must be 12 digits")  // Validation for Aadhaar number (12 digits)
    @Column(unique = true)
    private String aadhaarNumber;  

    private int leaveBalance;  

    @Column(columnDefinition = "int default 0")
    private int usedLeaveBalance = 0;
    
    private String token;
    
    @ManyToMany
    @JoinTable(
        name = "employee_roles", // Join table name
        joinColumns = {@JoinColumn(name = "employee_id")}, // Foreign key to Employee
        inverseJoinColumns = {@JoinColumn(name = "role_id")} // Foreign key to Role
    )
    private List<Role> roles; // List of roles for this employee
    
    @OneToMany(mappedBy = "employee", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Qualification> qualifications;

    @OneToMany(mappedBy = "employee", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Leave> leaves;

}
