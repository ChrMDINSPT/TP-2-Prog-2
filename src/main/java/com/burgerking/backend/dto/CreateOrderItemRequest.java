package com.burgerking.backend.dto;

import java.util.List;

public class CreateOrderItemRequest {

    private Long itemId;

    private List<Long> removedIngredientIds;

    private List<Long> addedIngredientIds;

    public CreateOrderItemRequest() {
    }

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public List<Long> getRemovedIngredientIds() {
        return removedIngredientIds;
    }

    public void setRemovedIngredientIds(
            List<Long> removedIngredientIds) {

        this.removedIngredientIds = removedIngredientIds;
    }

    public List<Long> getAddedIngredientIds() {
        return addedIngredientIds;
    }

    public void setAddedIngredientIds(
            List<Long> addedIngredientIds) {

        this.addedIngredientIds = addedIngredientIds;
    }
}