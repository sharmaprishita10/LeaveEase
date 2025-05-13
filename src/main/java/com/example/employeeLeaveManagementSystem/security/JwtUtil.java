package com.example.employeeLeaveManagementSystem.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.example.employeeLeaveManagementSystem.service.EmployeeService;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtUtil {

	@Value("${jwt.secret}")
	private String jwtSecret;

	@Value("${jwt.expiration}")
	private int jwtExpirationMs;

	private SecretKey key;

	@Autowired
	private EmployeeService empService;

	// Initializes the key after the class is instantiated and the jwtSecret is injected,
	// preventing the repeated creation of the key and enhancing performance
	@PostConstruct
	public void init() {
		this.key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
	}

	// Generate JWT token with roles claim from UserDetails
	public String generateToken(UserDetails userDetails) {
		// Extract roles as a list of strings 
		List<String> roles = userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority)
				.collect(Collectors.toList());

		return Jwts.builder().setSubject(userDetails.getUsername()).claim("roles", roles) 
				.setIssuedAt(new Date()).setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
				.signWith(key, SignatureAlgorithm.HS256).compact();
	}

	// Get username from JWT token
	public String getUsernameFromToken(String token) {
		return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().getSubject();
	}

	// Validate JWT token
	public boolean validateJwtToken(String token) {
		try {

			String username = getUsernameFromToken(token);
			if (empService.checkToken(username, token)) {
				Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
				return true;
			}
			return false;
		} catch (SecurityException e) {
			System.out.println("Invalid JWT signature: " + e.getMessage());
		} catch (MalformedJwtException e) {
			System.out.println("Invalid JWT token: " + e.getMessage());
		} catch (ExpiredJwtException e) {
			System.out.println("JWT token is expired: " + e.getMessage());
		} catch (UnsupportedJwtException e) {
			System.out.println("JWT token is unsupported: " + e.getMessage());
		} catch (IllegalArgumentException e) {
			System.out.println("JWT claims string is empty: " + e.getMessage());
		}
		return false;
	}

	public List<String> getRolesFromToken(String token) {
		Claims claims = Jwts.parserBuilder()
	            .setSigningKey(key)
	            .build()
	            .parseClaimsJws(token)
	            .getBody();

	    return claims.get("roles", List.class);
	}
}