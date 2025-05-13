package com.example.employeeLeaveManagementSystem.dto;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ViewProfileDto implements Serializable {

	private static final long serialVersionUID = 1L;

	private String empId;
	private String name;
	private String email;
	private String mobileNumber;
	private int managerId;
	private Date dob;
	private String address;
	private String profilePic; 			// Base64 Encoded String
	private String aadhaarNumber;  
	private int leaveBalance;
	private List<QualificationDto> qualifications;
}
