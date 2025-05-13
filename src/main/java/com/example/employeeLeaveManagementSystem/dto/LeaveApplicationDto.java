// Used while giving response for the pending leave applications to the manager

package com.example.employeeLeaveManagementSystem.dto;

import java.io.Serializable;
import java.sql.Date;

import com.example.employeeLeaveManagementSystem.model.LeaveType;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LeaveApplicationDto implements Serializable {

	private static final long serialVersionUID = 1L;

	private int employeeId;
	private LeaveType leaveType;
	private Date fromDate;
	private Date toDate;
	private int numberOfDays;
	private String reason;
}
