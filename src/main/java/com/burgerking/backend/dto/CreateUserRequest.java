package com.burgerking.backend.dto;

import com.burgerking.backend.entity.UserType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CreateUserRequest {

    @NotBlank(message = "El subject externo no puede estar vacío")
    private String externalSubject;
    @NotBlank(message = "El nombre no puede estar vacío")
    private String name;
    @NotNull(message = "El tipo de usuario es obligatorio")
    private UserType userType;

    public CreateUserRequest() {
    }

    public String getExternalSubject() {
        return externalSubject;
    }

    public void setExternalSubject(
            String externalSubject) {
        this.externalSubject = externalSubject;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UserType getUserType() {
        return userType;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }
}
