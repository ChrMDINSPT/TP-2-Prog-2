package com.burgerking.backend.service;

import com.burgerking.backend.entity.DailyRole;
import com.burgerking.backend.entity.Employee;
import com.burgerking.backend.repository.EmployeeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    public EmployeeService(
            EmployeeRepository employeeRepository) {

        this.employeeRepository = employeeRepository;
    }

    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    public Employee getEmployeeById(Long id) {
        return employeeRepository
                .findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Empleado no encontrado"));
    }

    public List<Employee> getEmployeesByRole(
            DailyRole role) {

        return employeeRepository
                .findByDailyRole(role);
    }

    public Employee changeDailyRole(
            Long id,
            DailyRole role) {

        Employee employee = getEmployeeById(id);

        employee.setDailyRole(role);

        return employeeRepository.save(employee);
    }
}