package com.burgerking.backend.dto;

import java.math.BigDecimal;
import java.util.List;

public class CreateItemRequest {

    private String name;
    private BigDecimal price;
    private List<Long> ingredientIds;

    public CreateItemRequest() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public List<Long> getIngredientIds() {
        return ingredientIds;
    }

    public void setIngredientIds(List<Long> ingredientIds) {
        this.ingredientIds = ingredientIds;
    }
}