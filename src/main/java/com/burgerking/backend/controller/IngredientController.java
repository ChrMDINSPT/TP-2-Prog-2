package com.burgerking.backend.controller;

import com.burgerking.backend.entity.Ingredient;
import com.burgerking.backend.service.IngredientService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @PostMapping
    public Ingredient createIngredient(
        @RequestBody Ingredient ingredient) {
            return ingredientService.createIngredient(ingredient);
    }
}