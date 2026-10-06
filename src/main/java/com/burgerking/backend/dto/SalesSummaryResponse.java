package com.burgerking.backend.dto;

import java.math.BigDecimal;

public class SalesSummaryResponse {

    private long totalOrders;
    private long totalItems;
    private BigDecimal totalRevenue;
    private BigDecimal averageOrderValue;

    public SalesSummaryResponse(
            long totalOrders,
            long totalItems,
            BigDecimal totalRevenue,
            BigDecimal averageOrderValue) {

        this.totalOrders = totalOrders;
        this.totalItems = totalItems;
        this.totalRevenue = totalRevenue;
        this.averageOrderValue = averageOrderValue;
    }

    public long getTotalOrders() {
        return totalOrders;
    }

    public long getTotalItems() {
        return totalItems;
    }

    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }

    public BigDecimal getAverageOrderValue() {
        return averageOrderValue;
    }
}