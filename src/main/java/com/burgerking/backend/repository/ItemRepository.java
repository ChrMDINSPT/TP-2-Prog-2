package com.burgerking.backend.repository;

import com.burgerking.backend.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepository
        extends JpaRepository<Item, Long> {
    List<Item> findByActiveTrue();
}