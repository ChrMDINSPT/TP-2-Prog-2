package com.burgerking.backend.service;

import com.burgerking.backend.dto.CreateItemRequest;
import com.burgerking.backend.entity.Ingredient;
import com.burgerking.backend.entity.Item;
import com.burgerking.backend.repository.IngredientRepository;
import com.burgerking.backend.repository.ItemRepository;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@Service
public class ItemService {

    private final ItemRepository itemRepository;
    private final IngredientRepository ingredientRepository;

    public ItemService(
            ItemRepository itemRepository,
            IngredientRepository ingredientRepository) {

        this.itemRepository = itemRepository;
        this.ingredientRepository = ingredientRepository;
    }

    public List<Item> getAllActiveItems() {
        return itemRepository.findByActiveTrue();
    }

    public Item getItemById(Long id) {
        return itemRepository
                .findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Item no encontrado")
                );
    }

    public Item createItem(CreateItemRequest request) {

        if (request.getPrice() == null
                || request.getPrice().signum() < 0) {
            throw new ResourceNotFoundException(
                    "El precio no puede ser negativo"
            );
        }

        List<Ingredient> ingredients =
                ingredientRepository.findAllById(
                        request.getIngredientIds()
                );

        if (ingredients.size()
                != request.getIngredientIds().size()) {

            throw new ResourceNotFoundException(
                    "Uno o mas ingredientes no existen"
            );
        }

        Item item = new Item();

        item.setName(request.getName());
        item.setPrice(request.getPrice());
        item.setActive(true);
        item.setIngredients(
                new HashSet<>(ingredients)
        );

        return itemRepository.save(item);
    }

    public Item updateItem(
            Long id,
            CreateItemRequest request) {

        Item item = getItemById(id);

        if (request.getPrice() == null
                || request.getPrice().signum() < 0) {
            throw new ResourceNotFoundException(
                    "El precio no puede ser negativo"
            );
        }

        List<Ingredient> ingredients =
                ingredientRepository.findAllById(
                        request.getIngredientIds()
                );

        if (ingredients.size()
                != request.getIngredientIds().size()) {

            throw new ResourceNotFoundException(
                    "Uno o mas ingredientes no existen"
            );
        }

        item.setName(request.getName());
        item.setPrice(request.getPrice());
        item.setIngredients(
                new HashSet<>(ingredients)
        );

        return itemRepository.save(item);
    }

    public void deactivateItem(Long id) {

        Item item = getItemById(id);

        item.setActive(false);

        itemRepository.save(item);
    }
}