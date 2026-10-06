package com.burgerking.backend.controller;

import com.burgerking.backend.dto.AssignCookRequest;
import com.burgerking.backend.dto.CreateOrderRequest;
import com.burgerking.backend.dto.OrderResponse;
import com.burgerking.backend.service.OrderService;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.*;

import java.util.List;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

  private final OrderService orderService;

  public OrderController(
      OrderService orderService) {

    this.orderService = orderService;
  }

  @GetMapping
  public List<OrderResponse> getAllOrders() {
    return orderService.getAllOrders();
  }

  @GetMapping("/{id}")
  public OrderResponse getOrderById(
      @PathVariable Long id) {

    return orderService.getOrderById(id);
  }

  @PostMapping
  public OrderResponse createOrder(
      @Valid @RequestBody CreateOrderRequest request) {

    return orderService.createOrder(request);
  }

  @PatchMapping("/{id}/assign-cook")
  public OrderResponse assignCook(
      @PathVariable Long id,
      @Valid @RequestBody AssignCookRequest request) {

    return orderService.assignCook(
        id,
        request.getCookId());
  }

  @PatchMapping("/{id}/start")
  public OrderResponse startOrder(
      @PathVariable Long id) {

    return orderService.startOrder(id);
  }

  @PatchMapping("/{id}/ready")
  public OrderResponse markReady(
      @PathVariable Long id) {

    return orderService.markReady(id);
  }

  @PatchMapping("/{id}/deliver")
  public OrderResponse deliverOrder(
      @PathVariable Long id) {

    return orderService.deliverOrder(id);
  }

  @PatchMapping("/{id}/cancel")
  public OrderResponse cancelOrder(
      @PathVariable Long id) {

    return orderService.cancelOrder(id);
  }
}