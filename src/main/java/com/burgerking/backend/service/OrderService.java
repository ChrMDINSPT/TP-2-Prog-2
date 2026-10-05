package com.burgerking.backend.service;

import com.burgerking.backend.dto.*;
import com.burgerking.backend.entity.*;
import com.burgerking.backend.repository.EmployeeRepository;
import com.burgerking.backend.repository.IngredientRepository;
import com.burgerking.backend.repository.ItemRepository;
import com.burgerking.backend.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final EmployeeRepository employeeRepository;
    private final ItemRepository itemRepository;
    private final IngredientRepository ingredientRepository;

    public OrderService(
            OrderRepository orderRepository,
            EmployeeRepository employeeRepository,
            ItemRepository itemRepository,
            IngredientRepository ingredientRepository) {

        this.orderRepository = orderRepository;
        this.employeeRepository = employeeRepository;
        this.itemRepository = itemRepository;
        this.ingredientRepository = ingredientRepository;
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getAllOrders() {
        return orderRepository
                .findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public OrderResponse getOrderById(Long id) {
        return toResponse(getOrderEntity(id));
    }

    @Transactional
    public OrderResponse createOrder(
            CreateOrderRequest request) {

        Employee seller = employeeRepository
                .findById(request.getSellerId())
                .orElseThrow(() -> new RuntimeException(
                        "Vendedor no encontrado"));

        if (seller.getDailyRole() != DailyRole.SELLER) {

            throw new RuntimeException(
                    "El empleado no tiene rol de vendedor");
        }

        if (request.getItems() == null
                || request.getItems().isEmpty()) {

            throw new RuntimeException(
                    "El pedido debe tener al menos un item");
        }

        Order order = new Order();

        order.setSeller(seller);
        order.setStatus(OrderStatus.RECEIVED);

        for (CreateOrderItemRequest requestedItem : request.getItems()) {

            Item menuItem = itemRepository
                    .findById(requestedItem.getItemId())
                    .orElseThrow(() -> new RuntimeException(
                            "Item no encontrado"));

            if (!menuItem.isActive()) {
                throw new RuntimeException(
                        "El item no esta activo");
            }

            Set<Ingredient> ingredients = new HashSet<>(
                    menuItem.getIngredients());

            Set<Long> removedIds = requestedItem.getRemovedIngredientIds() == null
                    ? Set.of()
                    : new HashSet<>(
                            requestedItem
                                    .getRemovedIngredientIds());

            Set<Long> baseIngredientIds = menuItem.getIngredients()
                    .stream()
                    .map(Ingredient::getId)
                    .collect(
                            java.util.stream.Collectors.toSet());

            for (Long removedId : removedIds) {
                if (!baseIngredientIds.contains(removedId)) {
                    throw new RuntimeException(
                            "Se intento quitar un ingrediente "
                                    + "que el item no posee");
                }
            }

            ingredients.removeIf(
                    ingredient -> removedIds.contains(
                            ingredient.getId()));

            Set<Long> addedIds = requestedItem.getAddedIngredientIds() == null
                    ? Set.of()
                    : new HashSet<>(
                            requestedItem
                                    .getAddedIngredientIds());

            if (!addedIds.isEmpty()) {

                List<Ingredient> extras = ingredientRepository
                        .findAllById(addedIds);

                if (extras.size() != addedIds.size()) {

                    throw new RuntimeException(
                            "Uno o mas ingredientes "
                                    + "agregados no existen");
                }

                ingredients.addAll(extras);
            }

            OrderItem orderItem = new OrderItem();

            orderItem.setItem(menuItem);

            orderItem.setItemName(
                    menuItem.getName());

            orderItem.setUnitPrice(
                    menuItem.getPrice());

            orderItem.setIngredients(ingredients);

            order.addItem(orderItem);
        }

        Order saved = orderRepository.save(order);

        return toResponse(saved);
    }

    @Transactional
    public OrderResponse assignCook(
            Long orderId,
            Long cookId) {

        Order order = getOrderEntity(orderId);

        if (order.getStatus() != OrderStatus.RECEIVED) {

            throw new RuntimeException(
                    "El pedido no esta disponible "
                            + "para asignacion");
        }

        if (order.getCook() != null) {
            throw new RuntimeException(
                    "El pedido ya tiene cocinero");
        }

        Employee cook = employeeRepository
                .findById(cookId)
                .orElseThrow(() -> new RuntimeException(
                        "Cocinero no encontrado"));

        if (cook.getDailyRole() != DailyRole.COOK) {

            throw new RuntimeException(
                    "El empleado no tiene rol de cocinero");
        }

        order.setCook(cook);

        return toResponse(
                orderRepository.save(order));
    }

    @Transactional
    public OrderResponse startOrder(Long id) {

        Order order = getOrderEntity(id);

        if (order.getStatus() != OrderStatus.RECEIVED) {

            throw new RuntimeException(
                    "El pedido no puede comenzar "
                            + "a prepararse");
        }

        if (order.getCook() == null) {
            throw new RuntimeException(
                    "El pedido no tiene cocinero asignado");
        }

        order.setStatus(
                OrderStatus.IN_PREPARATION);

        return toResponse(
                orderRepository.save(order));
    }

    @Transactional
    public OrderResponse markReady(Long id) {

        Order order = getOrderEntity(id);

        if (order.getStatus() != OrderStatus.IN_PREPARATION) {

            throw new RuntimeException(
                    "El pedido no esta en preparacion");
        }

        order.setStatus(OrderStatus.READY);

        return toResponse(
                orderRepository.save(order));
    }

    @Transactional
    public OrderResponse deliverOrder(Long id) {

        Order order = getOrderEntity(id);

        if (order.getStatus() != OrderStatus.READY) {

            throw new RuntimeException(
                    "El pedido no esta listo");
        }

        order.setStatus(
                OrderStatus.DELIVERED);

        order.setDeliveredAt(
                LocalDateTime.now());

        return toResponse(
                orderRepository.save(order));
    }

    @Transactional
    public OrderResponse cancelOrder(Long id) {

        Order order = getOrderEntity(id);

        if (order.getStatus() == OrderStatus.DELIVERED
                || order.getStatus() == OrderStatus.CANCELLED) {

            throw new RuntimeException(
                    "El pedido no puede cancelarse");
        }

        order.setStatus(
                OrderStatus.CANCELLED);

        return toResponse(
                orderRepository.save(order));
    }

    private Order getOrderEntity(Long id) {
        return orderRepository
                .findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Pedido no encontrado"));
    }

    private OrderResponse toResponse(Order order) {

        List<OrderItemResponse> itemResponses = order.getItems()
                .stream()
                .map(item -> new OrderItemResponse(
                        item.getId(),
                        item.getItem().getId(),
                        item.getItemName(),
                        item.getUnitPrice(),
                        item.getIngredients()
                                .stream()
                                .map(
                                        Ingredient::getName)
                                .sorted()
                                .toList()))
                .toList();

        BigDecimal total = order.getItems()
                .stream()
                .map(OrderItem::getUnitPrice)
                .reduce(
                        BigDecimal.ZERO,
                        BigDecimal::add);

        Long cookId = null;
        String cookName = null;

        if (order.getCook() != null) {
            cookId = order.getCook().getId();

            cookName = order.getCook()
                    .getUser()
                    .getName();
        }

        return new OrderResponse(
                order.getId(),
                order.getSeller().getId(),
                order.getSeller()
                        .getUser()
                        .getName(),
                cookId,
                cookName,
                order.getStatus(),
                order.getCreatedAt(),
                order.getDeliveredAt(),
                total,
                itemResponses);
    }
}