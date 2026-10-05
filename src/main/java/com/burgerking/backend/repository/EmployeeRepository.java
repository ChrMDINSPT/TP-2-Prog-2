package com.burgerking.backend.repository;

import com.burgerking.backend.entity.DailyRole;
import com.burgerking.backend.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmployeeRepository
        extends JpaRepository<Employee, Long> {

    List<Employee> findByDailyRole(
            DailyRole dailyRole
    );
}