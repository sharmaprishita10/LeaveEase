package com.example.employeeLeaveManagementSystem.dto;

import java.io.Serializable;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmployeeDto implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String empId;
	private String name;
	private String email;
	private String password;
	private int managerId;
	private int leaveBalance;
	private List<Integer> roles;
}
