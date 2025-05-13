package com.example.employeeLeaveManagementSystem.dto;

import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QualificationDto implements Serializable {

	private static final long serialVersionUID = 1L;

	private String degree;

	private String institute;

	private Integer passingYear;

	private String grade;
}
