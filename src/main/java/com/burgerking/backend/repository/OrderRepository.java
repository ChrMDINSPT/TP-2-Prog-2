package com.burgerking.backend.repository;

import com.burgerking.backend.entity.Order;
import com.burgerking.backend.entity.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderRepository
        extends JpaRepository<Order, Long> {

    List<Order> findByStatus(OrderStatus status);

    List<Order> findByCook_IdAndStatus(
            Long cookId,
            OrderStatus status
    );

    List<Order> findBySeller_Id(Long sellerId);

    List<Order> findBySeller_IdAndStatus(
            Long sellerId,
            OrderStatus status
    );

    List<Order> findByStatusAndDeliveredAtBetween(
            OrderStatus status,
            LocalDateTime from,
            LocalDateTime to
    );
}