package com.burgerking.backend.controller;

import com.burgerking.backend.dto.SaleResponse;
import com.burgerking.backend.dto.SalesSummaryResponse;
import com.burgerking.backend.service.SalesService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/sales")
public class SalesController {

    private final SalesService salesService;

    public SalesController(
            SalesService salesService) {

        this.salesService = salesService;
    }

    @GetMapping
    public List<SaleResponse> getAllSales() {
        return salesService.getAllSales();
    }

    @GetMapping("/summary")
    public SalesSummaryResponse getSummary() {
        return salesService.getSummary();
    }

    @GetMapping("/seller/{sellerId}")
    public List<SaleResponse> getSalesBySeller(
            @PathVariable Long sellerId) {

        return salesService
                .getSalesBySeller(sellerId);
    }

    @GetMapping("/range")
    public List<SaleResponse> getSalesByDateRange(
            @RequestParam
            @DateTimeFormat(
                iso = DateTimeFormat.ISO.DATE_TIME
            )
            LocalDateTime from,

            @RequestParam
            @DateTimeFormat(
                iso = DateTimeFormat.ISO.DATE_TIME
            )
            LocalDateTime to) {

        return salesService
                .getSalesByDateRange(
                        from,
                        to
                );
    }
}