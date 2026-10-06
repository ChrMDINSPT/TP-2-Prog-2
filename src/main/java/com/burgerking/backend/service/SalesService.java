package com.burgerking.backend.service;

import com.burgerking.backend.dto.SaleResponse;
import com.burgerking.backend.dto.SalesSummaryResponse;
import com.burgerking.backend.entity.Order;
import com.burgerking.backend.entity.OrderItem;
import com.burgerking.backend.entity.OrderStatus;
import com.burgerking.backend.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class SalesService {

    private final OrderRepository orderRepository;

    public SalesService(
            OrderRepository orderRepository) {

        this.orderRepository = orderRepository;
    }

    @Transactional(readOnly = true)
    public List<SaleResponse> getAllSales() {

        return orderRepository
                .findByStatus(OrderStatus.DELIVERED)
                .stream()
                .map(this::toSaleResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<SaleResponse> getSalesBySeller(
            Long sellerId) {

        return orderRepository
                .findBySeller_IdAndStatus(
                        sellerId,
                        OrderStatus.DELIVERED
                )
                .stream()
                .map(this::toSaleResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<SaleResponse> getSalesByDateRange(
            LocalDateTime from,
            LocalDateTime to) {

        return orderRepository
                .findByStatusAndDeliveredAtBetween(
                        OrderStatus.DELIVERED,
                        from,
                        to
                )
                .stream()
                .map(this::toSaleResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public SalesSummaryResponse getSummary() {

        List<Order> sales =
                orderRepository.findByStatus(
                        OrderStatus.DELIVERED
                );

        long totalOrders = sales.size();

        long totalItems =
                sales.stream()
                        .mapToLong(order ->
                                order.getItems().size()
                        )
                        .sum();

        BigDecimal totalRevenue =
                sales.stream()
                        .flatMap(order ->
                                order.getItems().stream()
                        )
                        .map(OrderItem::getUnitPrice)
                        .reduce(
                                BigDecimal.ZERO,
                                BigDecimal::add
                        );

        BigDecimal averageOrderValue =
                BigDecimal.ZERO;

        if (totalOrders > 0) {
            averageOrderValue =
                    totalRevenue.divide(
                            BigDecimal.valueOf(
                                    totalOrders
                            ),
                            2,
                            RoundingMode.HALF_UP
                    );
        }

        return new SalesSummaryResponse(
                totalOrders,
                totalItems,
                totalRevenue,
                averageOrderValue
        );
    }

    private SaleResponse toSaleResponse(
            Order order) {

        BigDecimal total =
                order.getItems()
                        .stream()
                        .map(OrderItem::getUnitPrice)
                        .reduce(
                                BigDecimal.ZERO,
                                BigDecimal::add
                        );

        Long cookId = null;
        String cookName = null;

        if (order.getCook() != null) {
            cookId =
                    order.getCook().getId();

            cookName =
                    order.getCook()
                            .getUser()
                            .getName();
        }

        return new SaleResponse(
                order.getId(),
                order.getSeller().getId(),
                order.getSeller()
                        .getUser()
                        .getName(),
                cookId,
                cookName,
                order.getDeliveredAt(),
                order.getItems().size(),
                total
        );
    }
}