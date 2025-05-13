package com.example.employeeLeaveManagementSystem.dto;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmployeeNameDto implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private String empId;
	private String name;
}
