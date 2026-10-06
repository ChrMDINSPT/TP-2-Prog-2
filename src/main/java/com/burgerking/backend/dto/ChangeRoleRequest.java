package com.burgerking.backend.dto;

import com.burgerking.backend.entity.DailyRole;
import jakarta.validation.constraints.NotNull;

public class ChangeRoleRequest {

    @NotNull(message = "El rol es obligatorio")
    private DailyRole role;

    public ChangeRoleRequest() {
    }

    public DailyRole getRole() {
        return role;
    }

    public void setRole(DailyRole role) {
        this.role = role;
    }
}
