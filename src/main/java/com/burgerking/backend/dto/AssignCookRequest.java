package com.burgerking.backend.dto;

import jakarta.validation.constraints.NotNull;

public class AssignCookRequest {

    @NotNull(message = "El cocinero es obligatorio")
    private Long cookId;

    public Long getCookId() {
        return cookId;
    }

    public void setCookId(Long cookId) {
        this.cookId = cookId;
    }
}