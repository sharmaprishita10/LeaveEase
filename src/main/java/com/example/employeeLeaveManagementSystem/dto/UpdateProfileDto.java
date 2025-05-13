package com.example.employeeLeaveManagementSystem.dto;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.example.employeeLeaveManagementSystem.model.Qualification;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateProfileDto implements Serializable{

	private static final long serialVersionUID = 1L;

	private String password;
	private Date dob;
	private String address;
	private String mobileNumber;
	private String aadhaarNumber; 
	private String profilePic; 			// Base64 Encoded String
	private List<Qualification> qualifications;
}
