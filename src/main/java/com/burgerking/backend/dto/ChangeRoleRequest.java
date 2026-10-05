package com.burgerking.backend.dto;

import com.burgerking.backend.entity.DailyRole;

public class ChangeRoleRequest {

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