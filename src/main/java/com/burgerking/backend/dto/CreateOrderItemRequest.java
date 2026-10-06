package com.burgerking.backend.dto;

import jakarta.validation.constraints.NotNull;

import java.util.Set;

public class CreateOrderItemRequest {

    @NotNull(message = "El item es obligatorio")
    private Long itemId;

    private Set<Long> removedIngredientIds;

    private Set<Long> addedIngredientIds;

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public Set<Long> getRemovedIngredientIds() {
        return removedIngredientIds;
    }

    public void setRemovedIngredientIds(Set<Long> removedIngredientIds) {
        this.removedIngredientIds = removedIngredientIds;
    }

    public Set<Long> getAddedIngredientIds() {
        return addedIngredientIds;
    }

    public void setAddedIngredientIds(Set<Long> addedIngredientIds) {
        this.addedIngredientIds = addedIngredientIds;
    }
}