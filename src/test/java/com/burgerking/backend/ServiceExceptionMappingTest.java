package com.burgerking.backend;

import com.burgerking.backend.dto.CreateItemRequest;
import com.burgerking.backend.dto.CreateOrderItemRequest;
import com.burgerking.backend.dto.CreateOrderRequest;
import com.burgerking.backend.dto.CreateUserRequest;
import com.burgerking.backend.entity.DailyRole;
import com.burgerking.backend.entity.Employee;
import com.burgerking.backend.entity.Item;
import com.burgerking.backend.entity.Order;
import com.burgerking.backend.entity.OrderStatus;
import com.burgerking.backend.entity.UserType;
import com.burgerking.backend.exception.BusinessRuleException;
import com.burgerking.backend.exception.ConflictException;
import com.burgerking.backend.exception.ResourceNotFoundException;
import com.burgerking.backend.repository.EmployeeRepository;
import com.burgerking.backend.repository.IngredientRepository;
import com.burgerking.backend.repository.ItemRepository;
import com.burgerking.backend.repository.OrderRepository;
import com.burgerking.backend.repository.UserRepository;
import com.burgerking.backend.service.ItemService;
import com.burgerking.backend.service.OrderService;
import com.burgerking.backend.service.UserService;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ServiceExceptionMappingTest {

    @Test
    void duplicateUserIsConflict() {
        UserRepository users = mock(UserRepository.class);
        when(users.existsByExternalSubject("subject")).thenReturn(true);

        CreateUserRequest request = new CreateUserRequest();
        request.setExternalSubject("subject");
        request.setName("Ana");
        request.setUserType(UserType.EMPLOYEE);

        assertThrows(ConflictException.class,
                () -> new UserService(users).createUser(request));
    }

    @Test
    void invalidItemPriceIsBusinessRule() {
        ItemService service = new ItemService(mock(ItemRepository.class),
                mock(IngredientRepository.class));
        CreateItemRequest request = new CreateItemRequest();
        request.setPrice(BigDecimal.ZERO);

        assertThrows(BusinessRuleException.class, () -> service.createItem(request));
    }

    @Test
    void emptyIngredientSetIsAllowedForAnItem() {
        IngredientRepository ingredients = mock(IngredientRepository.class);
        ItemRepository items = mock(ItemRepository.class);
        when(ingredients.findAllById(Set.of())).thenReturn(List.of());
        when(items.save(any(Item.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CreateItemRequest request = new CreateItemRequest();
        request.setName("Simple");
        request.setPrice(BigDecimal.ONE);
        request.setIngredientIds(Set.of());

        assertDoesNotThrow(() -> new ItemService(items, ingredients).createItem(request));
    }

    @Test
    void missingAddedIngredientIsNotFound() {
        OrderRepository orders = mock(OrderRepository.class);
        EmployeeRepository employees = mock(EmployeeRepository.class);
        ItemRepository items = mock(ItemRepository.class);
        IngredientRepository ingredients = mock(IngredientRepository.class);

        Employee seller = new Employee();
        seller.setDailyRole(DailyRole.SELLER);
        Item menuItem = new Item();
        menuItem.setActive(true);
        when(employees.findById(1L)).thenReturn(Optional.of(seller));
        when(items.findById(2L)).thenReturn(Optional.of(menuItem));
        when(ingredients.findAllById(Set.of(9L))).thenReturn(List.of());

        CreateOrderItemRequest orderItem = new CreateOrderItemRequest();
        orderItem.setItemId(2L);
        orderItem.setAddedIngredientIds(Set.of(9L));
        CreateOrderRequest request = new CreateOrderRequest();
        request.setSellerId(1L);
        request.setItems(List.of(orderItem));

        assertThrows(ResourceNotFoundException.class,
                () -> new OrderService(orders, employees, items, ingredients)
                        .createOrder(request));
    }

    @Test
    void invalidReadyAndDeliveryStatesAreConflicts() {
        OrderRepository orders = mock(OrderRepository.class);
        OrderService service = new OrderService(orders, mock(EmployeeRepository.class),
                mock(ItemRepository.class), mock(IngredientRepository.class));
        Order order = new Order();
        order.setStatus(OrderStatus.RECEIVED);
        when(orders.findById(1L)).thenReturn(Optional.of(order));

        assertThrows(ConflictException.class, () -> service.markReady(1L));
        assertThrows(ConflictException.class, () -> service.deliverOrder(1L));
    }
}
