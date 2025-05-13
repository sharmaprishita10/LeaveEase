package com.example.employeeLeaveManagementSystem.service;

import java.io.File;
import java.io.*;
import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamSource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import com.example.employeeLeaveManagementSystem.dto.ViewProfileDto;
import com.example.employeeLeaveManagementSystem.model.Employee;
import com.example.employeeLeaveManagementSystem.model.Qualification;
import com.example.employeeLeaveManagementSystem.repository.EmployeeRepository;

import jakarta.transaction.Transactional;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

@Service
@Transactional
public class EmployeeService implements UserDetailsService {

	@Autowired
	private EmployeeRepository empRepo;

	@Autowired
	ResourceLoader resourceLoader;

	// For Authentication (empId is the username)

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Employee emp = empRepo.findByEmpId(username);
		if (emp == null) {
			throw new UsernameNotFoundException("Employee Not Found With Employee ID: " + username);
		}

		List<GrantedAuthority> authorities = emp.getRoles().stream()
				.map(role -> new SimpleGrantedAuthority(role.getName().name())).collect(Collectors.toList());

		return new org.springframework.security.core.userdetails.User(emp.getEmpId(), emp.getPassword(), authorities);
	}

	// To insert the new token/null in employee table
	public void insertToken(String username, String token) {
		Employee emp = empRepo.findByEmpId(username);
		emp.setToken(token);
		empRepo.save(emp);
	}

	// Verify given token with the one stored in the db
	public boolean checkToken(String username, String token) {
		Employee emp = empRepo.findByEmpId(username);
		String authToken = emp.getToken();

		if (authToken.equals(token)) {
			return true;
		} else {
			return false;
		}
	}

	public Employee createEmployee(Employee emp) {
		return empRepo.save(emp);
	}

	public Employee findEmployee(int id) {
		return empRepo.findById(id).get();
	}

	public void updateProfile(Employee newEmp) {
		try {
			// Get the username of the currently authenticated user
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			String username = authentication.getName(); // This will give the username of the authenticated user

			// Get the full Employee entity based on the username
			Employee oldEmp = empRepo.findByEmpId(username);
			oldEmp.setPassword(newEmp.getPassword());
			oldEmp.setDob(newEmp.getDob());
			oldEmp.setAddress(newEmp.getAddress());
			oldEmp.setMobileNumber(newEmp.getMobileNumber());
			oldEmp.setProfilePic(newEmp.getProfilePic());
			oldEmp.setAadhaarNumber(newEmp.getAadhaarNumber());

			// Update Qualifications: replace existing ones with new list
			oldEmp.getQualifications().clear(); // Old qualifications will be deleted due to orphanRemoval

			for (Qualification qualification : newEmp.getQualifications()) {
				qualification.setEmployee(oldEmp); // Set employee reference for new qualification
				oldEmp.getQualifications().add(qualification);
			}

			empRepo.save(oldEmp); // Save the new employee profile to the database

		} catch (Exception e) {
			// Log the exception for debugging purposes
			e.printStackTrace();
			throw new RuntimeException("Error updating employee profile", e);
		}
	}

	public List<Employee> getManagerEmployees() {

		// Get the username of the currently authenticated user
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String username = authentication.getName(); // This will give the username of the authenticated user

		// Get the full Employee entity based on the username
		Employee manager = empRepo.findByEmpId(username);

		List<Employee> employees = empRepo.findByManager(manager);

		return employees;
	}

	public Employee getMyProfile() {

		// Get the username of the currently authenticated user
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String username = authentication.getName(); // This will give the username of the authenticated user

		// Get the full Employee entity based on the username
		return empRepo.findByEmpId(username);
	}

	public byte[] generateProfilePdf(ViewProfileDto dto) throws JRException, IOException {

		String template = "C:\\STS4\\workspace-spring-tool-suite-4-4.28.1.RELEASE\\EmployeeLeaveManagementSystem\\src\\main\\resources\\reports\\empDetails.jrxml";

		// 1. Compile JRXML to JasperReport
		JasperReport jasperReport = JasperCompileManager.compileReport(template);

		// 2. Prepare data source (single-object list)
		JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(Collections.singletonList(dto));

		// 3. Fill report (no extra parameters needed if fields match DTO)
		Map<String, Object> params = new HashMap<>();
		JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, params, dataSource);

		// 4. Export to PDF in-memory
		return JasperExportManager.exportReportToPdf(jasperPrint);

	}
}
