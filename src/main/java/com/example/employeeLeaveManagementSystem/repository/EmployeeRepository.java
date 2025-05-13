package com.example.employeeLeaveManagementSystem.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.employeeLeaveManagementSystem.model.Employee;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Integer>{

	Employee findByEmpId(String empId);
    boolean existsByEmpId(String empId);
    List<Employee> findByManager(Employee manager);
}
