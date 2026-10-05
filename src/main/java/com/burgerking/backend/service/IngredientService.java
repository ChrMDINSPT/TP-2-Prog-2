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

    public Ingredient getIngredientById(Long id) {
        return ingredientRepository
                .findById(id)
                .orElseThrow(() ->
                    new RuntimeException("Ingrediente no encontrado")
                );
    }

    public Ingredient updateIngredient(
        Long id,
        Ingredient updatedIngredient) {

        Ingredient ingredient = ingredientRepository
                .findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                            "Ingrediente no encontrado"
                        )
                );

        ingredient.setName(
                updatedIngredient.getName()
        );

        return ingredientRepository.save(ingredient);
    }

    public void deleteIngredient(Long id) {
        if (!ingredientRepository.existsById(id)) {
            throw new RuntimeException(
                    "Ingrediente no encontrado"
            );
        }
        ingredientRepository.deleteById(id);
    }

    public Ingredient createIngredient(Ingredient ingredient) {
        return ingredientRepository.save(ingredient);
    }
}