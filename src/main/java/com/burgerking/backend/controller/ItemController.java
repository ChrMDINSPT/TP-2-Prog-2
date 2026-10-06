package com.burgerking.backend.controller;

import com.burgerking.backend.dto.CreateItemRequest;
import com.burgerking.backend.entity.Item;
import com.burgerking.backend.service.ItemService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/items")
public class ItemController {

    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping
    public List<Item> getAllItems() {
        return itemService.getAllActiveItems();
    }

    @GetMapping("/{id}")
    public Item getItemById(@PathVariable Long id) {
        return itemService.getItemById(id);
    }

    @PostMapping
    public Item createItem(
            @Valid @RequestBody CreateItemRequest request) {

        return itemService.createItem(request);
    }

    @PutMapping("/{id}")
    public Item updateItem(
            @PathVariable Long id,
            @Valid @RequestBody CreateItemRequest request) {

        return itemService.updateItem(id, request);
    }

    @DeleteMapping("/{id}")
    public void deleteItem(@PathVariable Long id) {
        itemService.deactivateItem(id);
    }
}