package com.burgerking.backend.dto;

import java.util.List;

public class CreateOrderRequest {

    private Long sellerId;

    private List<CreateOrderItemRequest> items;

    public CreateOrderRequest() {
    }

    public Long getSellerId() {
        return sellerId;
    }

    public void setSellerId(Long sellerId) {
        this.sellerId = sellerId;
    }

    public List<CreateOrderItemRequest> getItems() {
        return items;
    }

    public void setItems(
            List<CreateOrderItemRequest> items) {

        this.items = items;
    }
}
