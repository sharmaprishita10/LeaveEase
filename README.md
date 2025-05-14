# LeaveEase

LeaveEase is a backend-only employee leave management system offering secure, JWT-protected REST APIs built with Java and Spring Boot using MySQL database.

### Description

This project organizes organizational hierarchy and leave processes through three distinct roles — **SUPERADMIN**, **ADMIN**, and **EMPLOYEE**. 
A superadmin provisions admins (who act as reporting managers), admins onboard employees, and employees manage their profiles and leaves. 
Employees can update and view their profile, apply for or withdraw leave requests, and review their personal leave history. 
Reporting managers can view their team members’ profiles and leave histories, as well as approve or reject pending leave applications. 
A dedicated reporting endpoint uses JasperReports to generate a downloadable PDF summary for any employee.

### Getting Started

APIs can be tested on http://localhost:8080 (default port).

### Specification

#### Authentication & Role Management
- Authentication is implemented using Spring Security with JWT tokens.
- Three roles with escalating privileges:
  - **SUPERADMIN**: Creates and manages ADMIN accounts.
  - **ADMIN**: Onboards EMPLOYEE users and oversees their leave requests.
  - **EMPLOYEE**: Updates profile data, applies for or withdraws leave, and views personal leave history.

#### User Onboarding & Profiles
- **Superadmin** workflow for creating manager (admin) accounts.
- **Admin** workflow for creating employee users.
- **Employee** can retrieve and modify their own profile details.

#### Leave Workflow
- Employees submit new leave requests specifying dates and type of leave.
- Employees may withdraw pending requests before approval.
- A personal leave history endpoint returns all leave records.

#### Manager Oversight
- Admins can list all their team members, and inspect individual details and leave histories.
- Admins receive pending leave requests and can approve or reject each one.

#### PDF Reporting
- A JasperReports integration generates a polished PDF report containing an employee’s profile information, suitable for printing.

### About

This project showcases backend development skills in Java and Spring Boot, including secure REST API design, JWT authentication, role management, ORM with Hibernate, and dynamic PDF reporting.
