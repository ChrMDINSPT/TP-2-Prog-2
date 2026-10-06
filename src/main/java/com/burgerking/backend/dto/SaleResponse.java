package com.burgerking.backend.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class SaleResponse {

    private Long orderId;

    private Long sellerId;
    private String sellerName;

    private Long cookId;
    private String cookName;

    private LocalDateTime deliveredAt;

    private int itemCount;

    private BigDecimal total;

    public SaleResponse(
            Long orderId,
            Long sellerId,
            String sellerName,
            Long cookId,
            String cookName,
            LocalDateTime deliveredAt,
            int itemCount,
            BigDecimal total) {

        this.orderId = orderId;
        this.sellerId = sellerId;
        this.sellerName = sellerName;
        this.cookId = cookId;
        this.cookName = cookName;
        this.deliveredAt = deliveredAt;
        this.itemCount = itemCount;
        this.total = total;
    }

    public Long getOrderId() {
        return orderId;
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

    public LocalDateTime getDeliveredAt() {
        return deliveredAt;
    }

    public int getItemCount() {
        return itemCount;
    }

    public BigDecimal getTotal() {
        return total;
    }
}