package com.burgerking.backend.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "employees")
public class Employee {

    @Id
    @Column(name = "user_id")
    private Long id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "daily_role", nullable = false)
    private DailyRole dailyRole = DailyRole.UNASSIGNED;

    public Employee() {
    }

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public DailyRole getDailyRole() {
        return dailyRole;
    }

    public void setDailyRole(DailyRole dailyRole) {
        this.dailyRole = dailyRole;
    }
}