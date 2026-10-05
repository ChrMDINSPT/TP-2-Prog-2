package com.burgerking.backend.service;

import com.burgerking.backend.entity.Ingredient;
import com.burgerking.backend.repository.IngredientRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IngredientService {

    private final IngredientRepository ingredientRepository;

    public IngredientService(
            IngredientRepository ingredientRepository) {
        this.ingredientRepository = ingredientRepository;
    }

    public List<Ingredient> getAllIngredients() {
        return ingredientRepository.findAll();
    }

    public Ingredient createIngredient(Ingredient ingredient) {
    return ingredientRepository.save(ingredient);
}
}