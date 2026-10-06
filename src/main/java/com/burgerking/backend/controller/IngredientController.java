package com.burgerking.backend.controller;

import com.burgerking.backend.entity.Ingredient;
import com.burgerking.backend.service.IngredientService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/ingredients")
public class IngredientController {

    private final IngredientService ingredientService;

    public IngredientController(
            IngredientService ingredientService) {
        this.ingredientService = ingredientService;
    }

    @GetMapping
    public List<Ingredient> getAllIngredients() {
        return ingredientService.getAllIngredients();
    }

    @GetMapping("/{id}")
    public Ingredient getIngredientById(@PathVariable Long id) {
        return ingredientService.getIngredientById(id);
    }

    @PostMapping
    public Ingredient createIngredient(
            @Valid @RequestBody Ingredient ingredient) {
        return ingredientService.createIngredient(ingredient);
    }

    @PutMapping("/{id}")
    public Ingredient updateIngredient(
            @PathVariable Long id,
            @Valid @RequestBody Ingredient ingredient) {
        return ingredientService.updateIngredient(
                id,
                ingredient);
    }

    @DeleteMapping("/{id}")
    public void deleteIngredient(
            @PathVariable Long id) {
        ingredientService.deleteIngredient(id);
    }
}