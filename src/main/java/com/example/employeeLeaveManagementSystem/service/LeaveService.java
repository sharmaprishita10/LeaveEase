package com.example.employeeLeaveManagementSystem.service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.example.employeeLeaveManagementSystem.model.Employee;
import com.example.employeeLeaveManagementSystem.model.Leave;
import com.example.employeeLeaveManagementSystem.model.LeaveStatus;
import com.example.employeeLeaveManagementSystem.model.LeaveType;
import com.example.employeeLeaveManagementSystem.repository.EmployeeRepository;
import com.example.employeeLeaveManagementSystem.repository.LeaveRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class LeaveService {
	
	@Autowired
	LeaveRepository leaveRepo;
	
	@Autowired
	private EmployeeRepository empRepo;
	
	public String applyForLeave(Leave leave) {
		// Get the username of the currently authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();  // This will give the username of the authenticated user
        
        // Get the full Employee entity based on the username
        Employee emp = empRepo.findByEmpId(username);  
		
        // For Paid Leave
        if(leave.getLeaveType() == LeaveType.PAID_LEAVE)
        {
        	// Convert java.util.Date to LocalDate
            LocalDate startDate = leave.getFromDate().toLocalDate();
            
            LocalDate endDate = leave.getToDate().toLocalDate();
            
            // Ensure end date is greater than or equal to start date
            if (endDate.isBefore(startDate)) {
                return "End date cannot be before the start date.";
            }
            
            // Calculate the number of days between
            int days = (int) (ChronoUnit.DAYS.between(startDate, endDate) + 1);
            
            int leaveBal = emp.getLeaveBalance();
            int usedLeaveBal = emp.getUsedLeaveBalance(); 		// Already applied leaves
            int actualLeaveBal = leaveBal - usedLeaveBal;
            
            if(days > actualLeaveBal)
            {
            	return String.format("You cannot apply for %d days leave. Your leave balance is %d.", days, actualLeaveBal);
            }
            else
            {
            	leave.setEmployee(emp);
            	leave.setStatus(LeaveStatus.PENDING);
            	
            	emp.setUsedLeaveBalance(usedLeaveBal + days);
            	leaveRepo.save(leave);
            	return "Leave applied successfully. Kindly wait for approval.";
            }
        }
        else 		// For LOP
        {
        	leave.setEmployee(emp);
        	leave.setStatus(LeaveStatus.PENDING);
        	leaveRepo.save(leave);
        	return "Leave applied successfully. Kindly wait for approval.";
        }
	}

	public String setLeaveStatus(String status, int id) {
		
		Leave appliedLeave = leaveRepo.findById(id).get();
		if(appliedLeave == null)
		{
			return "Incorrect leave id.";
		}
		
		if(appliedLeave.getStatus() == LeaveStatus.PENDING)
		{
			Employee emp = appliedLeave.getEmployee();
			
			if(status.equalsIgnoreCase("Approve"))
			{
				appliedLeave.setStatus(LeaveStatus.APPROVED);
				if(appliedLeave.getLeaveType() == LeaveType.PAID_LEAVE)
				{
					emp.setLeaveBalance(emp.getLeaveBalance() - appliedLeave.getNumberOfDays());
					emp.setUsedLeaveBalance(emp.getUsedLeaveBalance() - appliedLeave.getNumberOfDays());
				}
				leaveRepo.save(appliedLeave);
				return "Leave approved successfully.";
			}
			else if(status.equalsIgnoreCase("Reject"))
			{
				appliedLeave.setStatus(LeaveStatus.REJECTED);
				emp.setUsedLeaveBalance(emp.getUsedLeaveBalance() - appliedLeave.getNumberOfDays());
				leaveRepo.save(appliedLeave);
				return "Leave rejected successfully.";
			}
			else
			{
				return "Invalid input.";
			}
		}
		else
		{
			return "This is not a pending leave.";
		}
	}

	// Get leave applications to the manager
	public List<Leave> getLeaveApplications() {
		
		// Get the username of the currently authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();  // This will give the username of the authenticated user
        
        // Get the full Employee entity based on the username
        Employee manager = empRepo.findByEmpId(username); 
        
		List<Leave> pendingLeaves = leaveRepo.findPendingLeavesByManagerId(LeaveStatus.PENDING, manager.getId());
		return pendingLeaves;
	}

	public String withdrawLeave(int id) {
		
		// Get the username of the currently authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();  // This will give the username of the authenticated user
        
        // Get the full Employee entity based on the username
        Employee emp = empRepo.findByEmpId(username);  
        
		Leave appliedLeave = leaveRepo.findByIdAndEmployee_Id(id, emp.getId());
		
		if(appliedLeave == null)
		{
			return "Incorrect leave id.";
		}
		
		if(appliedLeave.getStatus() == LeaveStatus.APPROVED)
		{
			emp.setLeaveBalance(emp.getLeaveBalance() + appliedLeave.getNumberOfDays());
		}
		else if(appliedLeave.getStatus() == LeaveStatus.PENDING)
		{
			emp.setUsedLeaveBalance(emp.getUsedLeaveBalance() - appliedLeave.getNumberOfDays());
		}
		
		appliedLeave.setStatus(LeaveStatus.WITHDRAWN);
		leaveRepo.save(appliedLeave);
		return "Leave withdrawn successfully.";
	}

	// View leave history of a specific employee
	public List<Leave> getEmpLeaveHistory(int id, String status) {

		if(status.equalsIgnoreCase("all"))
		{
			return leaveRepo.findByEmployee_Id(id);
		}
		else
		{
			LeaveStatus leaveStatus = LeaveStatus.valueOf(status.toUpperCase());
            return leaveRepo.findByEmployee_IdAndStatus(id, leaveStatus);
		}
	}

	public List<Leave> getMyLeaveHistory(String status) {
		
		// Get the username of the currently authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();  // This will give the username of the authenticated user
        
        // Get the full Employee entity based on the username
        Employee emp = empRepo.findByEmpId(username);  
        
		if(status.equalsIgnoreCase("all"))
		{
			return leaveRepo.findByEmployee_Id(emp.getId());
		}
		else
		{
			LeaveStatus leaveStatus = LeaveStatus.valueOf(status.toUpperCase());
            return leaveRepo.findByEmployee_IdAndStatus(emp.getId(), leaveStatus);
		}
	}

}
