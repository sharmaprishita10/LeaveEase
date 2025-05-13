// Used while giving response for the leave history of the employee

package com.example.employeeLeaveManagementSystem.dto;

import java.io.Serializable;
import java.sql.Date;

import com.example.employeeLeaveManagementSystem.model.LeaveStatus;
import com.example.employeeLeaveManagementSystem.model.LeaveType;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LeaveHistoryDto implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private LeaveType leaveType;
	private Date fromDate;
	private Date toDate;
	private int numberOfDays;
	private String reason;
	private LeaveStatus status;
}
