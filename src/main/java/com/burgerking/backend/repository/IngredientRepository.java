package com.burgerking.backend.repository;

import com.burgerking.backend.entity.Ingredient;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IngredientRepository
        extends JpaRepository<Ingredient, Long> {
}