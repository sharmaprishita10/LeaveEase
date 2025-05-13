package com.example.employeeLeaveManagementSystem.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.example.employeeLeaveManagementSystem.model.ApiResponse;
import com.example.employeeLeaveManagementSystem.model.Employee;
import com.example.employeeLeaveManagementSystem.repository.EmployeeRepository;
import com.example.employeeLeaveManagementSystem.security.JwtUtil;
import com.example.employeeLeaveManagementSystem.service.EmployeeService;


@RestController
@RequestMapping("/auth")
public class AuthController {
	
    @Autowired
    AuthenticationManager authenticationManager;
    
    @Autowired
    EmployeeRepository empRepo;
    
	@Autowired
    private EmployeeService empService;
	
    @Autowired
    PasswordEncoder encoder;
    
    @Autowired
    JwtUtil jwtUtils;
    
    
    @PostMapping("/signin")
    public ResponseEntity<ApiResponse> authenticateUser(@RequestBody Employee emp) {
        
    	try {
    	Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        emp.getEmpId(),
                        emp.getPassword()
                )
        );
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        
        String username = userDetails.getUsername();
        String token = jwtUtils.generateToken(userDetails);
        
        empService.insertToken(username, token);		// Sign in
        
        ApiResponse response = new ApiResponse("Authentication successful", token, HttpStatus.OK.value());
        return ResponseEntity.ok(response);
    	}
    	catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse("Invalid username or password", HttpStatus.UNAUTHORIZED.value()));
        } 
    	catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("Authentication failed due to an error", HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }
    
    @PostMapping("/signout")
    public ResponseEntity<ApiResponse> logoutUser() {
        
        // Get the username of the currently authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();  // This will give the username of the authenticated user
        
        empService.insertToken(username, null);			// Sign out
        
        ApiResponse response = new ApiResponse("You have been logged out.", HttpStatus.OK.value());
        return ResponseEntity.ok(response);
    }
}