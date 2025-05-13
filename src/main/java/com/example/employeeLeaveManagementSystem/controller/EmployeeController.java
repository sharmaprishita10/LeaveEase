package com.example.employeeLeaveManagementSystem.controller;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.employeeLeaveManagementSystem.dto.EmployeeDto;
import com.example.employeeLeaveManagementSystem.dto.EmployeeNameDto;
import com.example.employeeLeaveManagementSystem.dto.LeaveApplicationDto;
import com.example.employeeLeaveManagementSystem.dto.LeaveHistoryDto;
import com.example.employeeLeaveManagementSystem.dto.LeaveStatusDto;
import com.example.employeeLeaveManagementSystem.dto.Mapper;
import com.example.employeeLeaveManagementSystem.dto.UpdateProfileDto;
import com.example.employeeLeaveManagementSystem.dto.ViewProfileDto;
import com.example.employeeLeaveManagementSystem.model.ApiResponse;
import com.example.employeeLeaveManagementSystem.model.Employee;
import com.example.employeeLeaveManagementSystem.model.Leave;
import com.example.employeeLeaveManagementSystem.service.EmployeeService;
import com.example.employeeLeaveManagementSystem.service.LeaveService;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.repo.Resource;

@RestController
public class EmployeeController {

	@Autowired
	private EmployeeService empService;

	@Autowired
	private LeaveService leaveService;

	@Autowired
	PasswordEncoder encoder;

	// Creating new employee
	@PreAuthorize("hasAnyRole('ROLE_SUPERADMIN', 'ROLE_ADMIN')")
	@PostMapping("/register-employee")
	public ResponseEntity<ApiResponse> addNewEmployee(@RequestBody EmployeeDto empDto) {
		ApiResponse response;
		try {
			// Encrypt password
			empDto.setPassword(encoder.encode(empDto.getPassword()));
			Employee emp = Mapper.empDtoToEmployee(empDto);

			empService.createEmployee(emp);
			response = new ApiResponse("New employee registered successfully!", HttpStatus.CREATED.value());
			return new ResponseEntity<ApiResponse>(response, HttpStatus.CREATED);
		} catch (DataIntegrityViolationException e) {
			e.printStackTrace(); // Log the error for debugging
			String errorMessage = e.getMostSpecificCause().getMessage();
			int index = errorMessage.indexOf(" for");
			String result = (index != -1) ? errorMessage.substring(0, index) : errorMessage;

			response = new ApiResponse(result, HttpStatus.OK.value());
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			e.printStackTrace(); // Log the error for debugging
			response = new ApiResponse("Internal Server Error.", HttpStatus.INTERNAL_SERVER_ERROR.value());
			return new ResponseEntity<ApiResponse>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	// View a specific employee's details
	@PreAuthorize("hasAnyRole('ROLE_SUPERADMIN', 'ROLE_ADMIN')")
	@GetMapping("/view-employee-details/{id}")
	public ResponseEntity<ApiResponse> getById(@PathVariable int id) {

		ApiResponse response;
		try {

			Employee emp = empService.findEmployee(id);
			ViewProfileDto empDto = Mapper.entityToDto(emp);
			response = new ApiResponse("Fetch successful.", empDto, HttpStatus.OK.value());
			return ResponseEntity.ok(response);
		} catch (NoSuchElementException e) {
			e.printStackTrace(); // Log the error for debugging
			response = new ApiResponse("No such employee found.", HttpStatus.NOT_FOUND.value());
			return new ResponseEntity<ApiResponse>(response, HttpStatus.NOT_FOUND);
		}
	}

	// View my profile
	@GetMapping("/my-profile")
	public ResponseEntity<ApiResponse> getProfile() {

		ApiResponse response;
		Employee emp = empService.getMyProfile();

		ViewProfileDto empDto = Mapper.entityToDto(emp);
		response = new ApiResponse("Fetch successful.", empDto, HttpStatus.OK.value());
		return ResponseEntity.ok(response);
	}

	// Update your own profile
	@PutMapping("/update-profile")
	public ResponseEntity<ApiResponse> updateProfile(@RequestBody UpdateProfileDto empDto) {
		ApiResponse response;
		try {

			// Encrypt password
			empDto.setPassword(encoder.encode(empDto.getPassword()));

			Employee emp = Mapper.updateProfileDtoToEntity(empDto);

			empService.updateProfile(emp);
			response = new ApiResponse("Profile updated successfully.", HttpStatus.OK.value());
			return ResponseEntity.ok(response);
		} catch (DataIntegrityViolationException e) {
			e.printStackTrace(); // Log the error for debugging
			String errorMessage = e.getMostSpecificCause().getMessage();
			int index = errorMessage.indexOf(" for");
			String result = (index != -1) ? errorMessage.substring(0, index) : errorMessage;

			response = new ApiResponse(result, HttpStatus.OK.value());
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			e.printStackTrace(); // Log the error for debugging
			response = new ApiResponse("Internal Server Error.", HttpStatus.INTERNAL_SERVER_ERROR.value());
			return new ResponseEntity<ApiResponse>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	// Apply for leave
	@PutMapping("/apply-leave")
	public ResponseEntity<ApiResponse> applyForLeave(@RequestBody Leave leave) {
		ApiResponse response;
		try {
			String message = leaveService.applyForLeave(leave);
			response = new ApiResponse(message, HttpStatus.OK.value());
			return ResponseEntity.ok(response);
		} catch (DataIntegrityViolationException e) {
			e.printStackTrace(); // Log the error for debugging
			String errorMessage = e.getMostSpecificCause().getMessage();
			int index = errorMessage.indexOf(" for");
			String result = (index != -1) ? errorMessage.substring(0, index) : errorMessage;

			response = new ApiResponse(result, HttpStatus.OK.value());
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			e.printStackTrace(); // Log the error for debugging
			response = new ApiResponse("Internal Server Error.", HttpStatus.INTERNAL_SERVER_ERROR.value());
			return new ResponseEntity<ApiResponse>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	// Approve/Reject leave
	@PreAuthorize("hasAnyRole('ROLE_SUPERADMIN', 'ROLE_ADMIN')")
	@PutMapping("/set-leave-status/{id}") // Here, id means leave's id
	public ResponseEntity<ApiResponse> setLeaveStatus(@RequestBody LeaveStatusDto status, @PathVariable int id) {
		ApiResponse response;
		try {
			String message = leaveService.setLeaveStatus(status.getStatus(), id);
			response = new ApiResponse(message, HttpStatus.OK.value());
			return ResponseEntity.ok(response);
		} catch (NoSuchElementException e) {
			e.printStackTrace(); // Log the error for debugging
			response = new ApiResponse("Incorrect leave id.", HttpStatus.NOT_FOUND.value());
			return new ResponseEntity<ApiResponse>(response, HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			e.printStackTrace(); // Log the error for debugging
			response = new ApiResponse("Internal Server Error.", HttpStatus.INTERNAL_SERVER_ERROR.value());
			return new ResponseEntity<ApiResponse>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	// View all leave applications by manager's employees
	@PreAuthorize("hasAnyRole('ROLE_SUPERADMIN', 'ROLE_ADMIN')")
	@GetMapping("/leave-applications")
	public ResponseEntity<ApiResponse> viewLeaveApplications() {

		List<Leave> pendingLeaves = leaveService.getLeaveApplications();

		List<LeaveApplicationDto> leaveDtos = pendingLeaves.stream().map(Mapper::leaveToLeaveDto)
				.collect(Collectors.toList());

		ApiResponse response = new ApiResponse("Fetch successful.", leaveDtos, HttpStatus.OK.value());
		return ResponseEntity.ok(response);
	}

	// Withdraw leave
	@PutMapping("/withdraw-leave")
	public ResponseEntity<ApiResponse> withdrawLeave(@RequestBody int id) {
		ApiResponse response;
		try {
			String message = leaveService.withdrawLeave(id);
			response = new ApiResponse(message, HttpStatus.OK.value());
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			e.printStackTrace(); // Log the error for debugging
			response = new ApiResponse("Internal Server Error.", HttpStatus.INTERNAL_SERVER_ERROR.value());
			return new ResponseEntity<ApiResponse>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	// View all employees under the manager
	@PreAuthorize("hasAnyRole('ROLE_SUPERADMIN', 'ROLE_ADMIN')")
	@GetMapping("/view-employees")
	public ResponseEntity<ApiResponse> viewManagerEmployees() {

		List<Employee> employees = empService.getManagerEmployees();

		List<EmployeeNameDto> empDtos = employees.stream().map(Mapper::employeeToEmployeeNameDto)
				.collect(Collectors.toList());

		ApiResponse response = new ApiResponse("Fetch successful.", empDtos, HttpStatus.OK.value());
		return ResponseEntity.ok(response);
	}

	// View leave history of a specific employee
	@PreAuthorize("hasAnyRole('ROLE_SUPERADMIN', 'ROLE_ADMIN')")
	@GetMapping("/employee/{id}/leave-history/{status}")
	public ResponseEntity<ApiResponse> viewEmpLeaveHistory(@PathVariable int id, @PathVariable String status) {

		List<Leave> leaves = leaveService.getEmpLeaveHistory(id, status);

		List<LeaveHistoryDto> leaveDtos = leaves.stream().map(Mapper::leaveToLeaveHistoryDto)
				.collect(Collectors.toList());

		ApiResponse response = new ApiResponse("Fetch successful.", leaveDtos, HttpStatus.OK.value());
		return ResponseEntity.ok(response);
	}

	// View my leave history
	@GetMapping("/my-leave-history/{status}")
	public ResponseEntity<ApiResponse> viewMyLeaveHistory(@PathVariable String status) {

		List<Leave> leaves = leaveService.getMyLeaveHistory(status);

		List<LeaveHistoryDto> leaveDtos = leaves.stream().map(Mapper::leaveToLeaveHistoryDto)
				.collect(Collectors.toList());

		ApiResponse response = new ApiResponse("Fetch successful.", leaveDtos, HttpStatus.OK.value());
		return ResponseEntity.ok(response);
	}

	// Jasper Report
	// View a specific employee's details
	@PreAuthorize("hasAnyRole('ROLE_SUPERADMIN', 'ROLE_ADMIN')")
	@GetMapping("/employee-report/{id}")
    public ResponseEntity<ByteArrayResource> getProfilePdf(@PathVariable int id) throws JRException, IOException{
        // Fetch entity, convert to DTO
		Employee emp = empService.findEmployee(id);
        ViewProfileDto dto = Mapper.entityToDto(emp);

        // Generate PDF
        byte[] reportContent = empService.generateProfilePdf(dto);

        // Return as application/pdf
        ByteArrayResource resource = new ByteArrayResource(reportContent);
        
        return ResponseEntity.ok()
            .contentType(MediaType.APPLICATION_PDF)
            .contentLength(resource.contentLength())
            .header(HttpHeaders.CONTENT_DISPOSITION,
            ContentDisposition.attachment()
                .filename("employee_profile_" + id + ".pdf")
                .build().toString())
            .body(resource);                                                                                          
    }
}
