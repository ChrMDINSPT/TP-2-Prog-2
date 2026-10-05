package com.burgerking.backend.dto;

import com.burgerking.backend.entity.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class OrderResponse {

    private Long id;

    private Long sellerId;
    private String sellerName;

    private Long cookId;
    private String cookName;

    private OrderStatus status;

    private LocalDateTime createdAt;
    private LocalDateTime deliveredAt;

    private BigDecimal total;

    private List<OrderItemResponse> items;

    public OrderResponse(
            Long id,
            Long sellerId,
            String sellerName,
            Long cookId,
            String cookName,
            OrderStatus status,
            LocalDateTime createdAt,
            LocalDateTime deliveredAt,
            BigDecimal total,
            List<OrderItemResponse> items) {

        this.id = id;
        this.sellerId = sellerId;
        this.sellerName = sellerName;
        this.cookId = cookId;
        this.cookName = cookName;
        this.status = status;
        this.createdAt = createdAt;
        this.deliveredAt = deliveredAt;
        this.total = total;
        this.items = items;
    }

    public Long getId() {
        return id;
    }

    public Long getSellerId() {
        return sellerId;
    }

    public String getSellerName() {
        return sellerName;
    }

    public Long getCookId() {
        return cookId;
    }

    public String getCookName() {
        return cookName;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getDeliveredAt() {
        return deliveredAt;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public List<OrderItemResponse> getItems() {
        return items;
    }
}