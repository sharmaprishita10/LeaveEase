package com.example.employeeLeaveManagementSystem.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.employeeLeaveManagementSystem.model.Leave;
import com.example.employeeLeaveManagementSystem.model.LeaveStatus;

@Repository
public interface LeaveRepository extends JpaRepository<Leave, Integer>{
	@Query("SELECT l FROM Leave l WHERE l.status = :status AND l.employee.manager.id = :managerId")
    List<Leave> findPendingLeavesByManagerId(@Param("status") LeaveStatus status,
                                             @Param("managerId") int managerId);

	List<Leave> findByEmployee_Id(int id);

	List<Leave> findByEmployee_IdAndStatus(int id, LeaveStatus leaveStatus);

	Leave findByIdAndEmployee_Id(int id, int employeeId);		// For withdrawing leave
}
