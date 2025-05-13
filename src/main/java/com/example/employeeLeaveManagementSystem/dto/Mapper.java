package com.example.employeeLeaveManagementSystem.dto;

import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

import com.example.employeeLeaveManagementSystem.model.Employee;
import com.example.employeeLeaveManagementSystem.model.Leave;
import com.example.employeeLeaveManagementSystem.model.Qualification;
import com.example.employeeLeaveManagementSystem.model.Role;

public class Mapper {

	// Used while creating a new employee by the Admin for the request body
	public static Employee empDtoToEmployee(EmployeeDto empDto) {
		Employee emp = new Employee();

		emp.setEmpId(empDto.getEmpId());
		emp.setName(empDto.getName());
		emp.setEmail(empDto.getEmail());
		emp.setPassword(empDto.getPassword());
		emp.setLeaveBalance(empDto.getLeaveBalance());

		Employee manager = new Employee();
		manager.setId(empDto.getManagerId());
		emp.setManager(manager);

		// Convert list of role IDs to Role objects
		List<Role> roleList = empDto.getRoles().stream().map(roleId -> {
			Role role = new Role();
			role.setId(roleId);
			return role;
		}).collect(Collectors.toList());

		emp.setRoles(roleList);

		return emp;
	}

	// Used while updating employee profile (made for Base64 encoded string for profile pic)
	public static Employee updateProfileDtoToEntity(UpdateProfileDto empDto) {
		Employee emp = new Employee();
		emp.setPassword(empDto.getPassword());
		emp.setDob(empDto.getDob());
		emp.setAddress(empDto.getAddress());
		emp.setMobileNumber(empDto.getMobileNumber());
		emp.setAadhaarNumber(empDto.getAadhaarNumber());

		// Converting Base64 encoded string to byte[]
		if (empDto.getProfilePic() != null) {
			emp.setProfilePic(Base64.getDecoder().decode(empDto.getProfilePic()));
		}

		emp.setQualifications(empDto.getQualifications());
		return emp;
	}

	// Used while sending the employee profile in the API response
	public static ViewProfileDto entityToDto(Employee emp) {
		ViewProfileDto empDto = new ViewProfileDto();

		empDto.setEmpId(emp.getEmpId());
		empDto.setName(emp.getName());
		empDto.setEmail(emp.getEmail());
		empDto.setMobileNumber(emp.getMobileNumber());
		empDto.setManagerId(emp.getManager().getId());
		empDto.setDob(emp.getDob());
		empDto.setAddress(emp.getAddress());
		
		// Converting byte[] to Base 64 encoded string
		if (emp.getProfilePic() != null) {
			empDto.setProfilePic(Base64.getEncoder().encodeToString(emp.getProfilePic()));
		}
		else
		{
			empDto.setProfilePic(null);
		}
		empDto.setAadhaarNumber(emp.getAadhaarNumber());
		empDto.setLeaveBalance(emp.getLeaveBalance());

		List<QualificationDto> qualifications = emp.getQualifications().stream()
				.map(qualification -> qualificationToDto(qualification)).collect(Collectors.toList());

		empDto.setQualifications(qualifications);

		return empDto;
	}

	// Used for retrieving only relevant fields of the Qualification entity
	public static QualificationDto qualificationToDto(Qualification qualification) {
		QualificationDto qualificationDto = new QualificationDto();

		qualificationDto.setDegree(qualification.getDegree());
		qualificationDto.setInstitute(qualification.getInstitute());
		qualificationDto.setPassingYear(qualification.getPassingYear());
		qualificationDto.setGrade(qualification.getGrade());

		return qualificationDto;
	}

	// Used for displaying the pending leave applications to the manager
	public static LeaveApplicationDto leaveToLeaveDto(Leave leave) {
		LeaveApplicationDto leaveDto = new LeaveApplicationDto();

		leaveDto.setEmployeeId(leave.getEmployee().getId());
		leaveDto.setLeaveType(leave.getLeaveType());
		leaveDto.setFromDate(leave.getFromDate());
		leaveDto.setToDate(leave.getToDate());
		leaveDto.setNumberOfDays(leave.getNumberOfDays());
		leaveDto.setReason(leave.getReason());

		return leaveDto;
	}

	// Used for displaying the leave history of the employee
	public static LeaveHistoryDto leaveToLeaveHistoryDto(Leave leave) {
		LeaveHistoryDto leaveDto = new LeaveHistoryDto();

		leaveDto.setLeaveType(leave.getLeaveType());
		leaveDto.setFromDate(leave.getFromDate());
		leaveDto.setToDate(leave.getToDate());
		leaveDto.setNumberOfDays(leave.getNumberOfDays());
		leaveDto.setReason(leave.getReason());
		leaveDto.setStatus(leave.getStatus());
		return leaveDto;
	}

	public static EmployeeNameDto employeeToEmployeeNameDto(Employee emp) {
		EmployeeNameDto empDto = new EmployeeNameDto();

		empDto.setEmpId(emp.getEmpId());
		empDto.setName(emp.getName());

		return empDto;
	}
}
