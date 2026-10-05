package com.burgerking.backend.dto;

import com.burgerking.backend.entity.UserType;

public class CreateUserRequest {

    private String externalSubject;
    private String name;
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