package com.burgerking.backend.controller;

import com.burgerking.backend.dto.ChangeRoleRequest;
import com.burgerking.backend.entity.DailyRole;
import com.burgerking.backend.entity.Employee;
import com.burgerking.backend.service.EmployeeService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(
            EmployeeService employeeService) {

        this.employeeService = employeeService;
    }

    @GetMapping
    public List<Employee> getAllEmployees() {
        return employeeService.getAllEmployees();
    }

    @GetMapping("/{id}")
    public Employee getEmployeeById(
            @PathVariable Long id) {

        return employeeService
                .getEmployeeById(id);
    }

    @GetMapping("/role/{role}")
    public List<Employee> getEmployeesByRole(
            @PathVariable DailyRole role) {

        return employeeService
                .getEmployeesByRole(role);
    }

    @PatchMapping("/{id}/role")
    public Employee changeRole(
            @PathVariable Long id,
            @Valid @RequestBody ChangeRoleRequest request) {

        return employeeService.changeDailyRole(
                id,
                request.getRole());
    }
}