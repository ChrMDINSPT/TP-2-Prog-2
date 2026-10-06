package com.burgerking.backend.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public class CreateOrderRequest {

    @NotNull(message = "El vendedor es obligatorio")
    private Long sellerId;

    @NotEmpty(message = "El pedido debe tener al menos un item")
    private List<@NotNull(message = "El item del pedido es obligatorio") @Valid CreateOrderItemRequest> items;

    public Long getSellerId() {
        return sellerId;
    }

    public void setSellerId(Long sellerId) {
        this.sellerId = sellerId;
    }

    public List<CreateOrderItemRequest> getItems() {
        return items;
    }

    public void setItems(List<CreateOrderItemRequest> items) {
        this.items = items;
    }
}
