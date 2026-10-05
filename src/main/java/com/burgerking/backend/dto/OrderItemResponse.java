package com.burgerking.backend.dto;

import java.math.BigDecimal;
import java.util.List;

public class OrderItemResponse {

    private Long id;
    private Long itemId;
    private String itemName;
    private BigDecimal unitPrice;
    private List<String> ingredients;

    public OrderItemResponse(
            Long id,
            Long itemId,
            String itemName,
            BigDecimal unitPrice,
            List<String> ingredients) {

        this.id = id;
        this.itemId = itemId;
        this.itemName = itemName;
        this.unitPrice = unitPrice;
        this.ingredients = ingredients;
    }

    public Long getId() {
        return id;
    }

    public Long getItemId() {
        return itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public List<String> getIngredients() {
        return ingredients;
    }
}