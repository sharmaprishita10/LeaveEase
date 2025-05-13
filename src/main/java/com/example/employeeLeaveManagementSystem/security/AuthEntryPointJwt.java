package com.example.employeeLeaveManagementSystem.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.example.employeeLeaveManagementSystem.model.ApiResponse;

import java.io.IOException;

@Component
public class AuthEntryPointJwt  implements AuthenticationEntryPoint {
    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException
    ) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        
        // Set content type to application/json
        response.setContentType("application/json");
        
        // Create a custom response body
        String message = "Error: Unauthorized";
        
        ApiResponse responseBody = new ApiResponse(message, HttpServletResponse.SC_UNAUTHORIZED);
       
		response.getWriter().write(responseBody.toString());
		
	}
}