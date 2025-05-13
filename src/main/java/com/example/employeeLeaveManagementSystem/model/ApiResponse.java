package com.example.employeeLeaveManagementSystem.model;

import lombok.Data;

@Data
public class ApiResponse {
	
	private String message;
    private Object data;
    private int statusCode;
    
	public ApiResponse(String message, Object data, int statusCode) {
		this.message = message;
		this.data = data;
		this.statusCode = statusCode;
	}

	
	public ApiResponse(String message, int statusCode) {
		this.message = message;
		this.statusCode = statusCode;
	}

	@Override
	public String toString() {
		return "{\"message\": \"" + message + "\", \"data\": " + data + ", \"statusCode\": " + statusCode + "}";
	}
}
