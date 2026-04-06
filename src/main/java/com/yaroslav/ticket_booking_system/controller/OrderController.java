package com.yaroslav.ticket_booking_system.controller;

import com.yaroslav.ticket_booking_system.dto.OrderRequestDto;
import com.yaroslav.ticket_booking_system.dto.OrderResponseDto;
import com.yaroslav.ticket_booking_system.dto.OrderUpdateDto;
import com.yaroslav.ticket_booking_system.dto.TicketRequestDto;
import com.yaroslav.ticket_booking_system.model.OrderStatus;
import com.yaroslav.ticket_booking_system.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
@Tag(name = "Order Management", description = "APIs for managing orders in the ticket booking system")
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @Operation(summary = "Create a new order", description = "Creates a new order with selected tickets for a user")
    public ResponseEntity<OrderResponseDto> createOrder(@Valid @RequestBody OrderRequestDto requestDto) {

        final OrderResponseDto created = orderService.createOrder(requestDto);

        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get order by ID", description = "Retrieves detailed information about a specific order")
    public ResponseEntity<OrderResponseDto> getOrderById(@PathVariable UUID id) {

        final OrderResponseDto order = orderService.getOrderById(id);

        return ResponseEntity.ok(order);
    }

    @GetMapping("/status/{orderStatus}")
    @Operation(summary = "Get orders by status", description = "Retrieves all orders with a specific status (CREATED, PAID, CANCELLED, REFUNDED)")
    public ResponseEntity<List<OrderResponseDto>> getOrdersByStatus(@PathVariable OrderStatus orderStatus) {

        final List<OrderResponseDto> orders = orderService.getOrdersByStatus(orderStatus);

        return ResponseEntity.ok(orders);
    }

    @GetMapping("/deleted/{deleted}")
    @Operation(summary = "Get orders by deleted flag", description = "Retrieves orders based on soft-delete status")
    public ResponseEntity<List<OrderResponseDto>> getOrdersByDeleted(@PathVariable Boolean deleted) {

        final List<OrderResponseDto> orders = orderService.getOrdersByDeleted(deleted);

        return ResponseEntity.ok(orders);
    }

    @GetMapping("/by-date")
    @Operation(summary = "Get orders by completion date range", description = "Retrieves orders completed between two dates")
    public ResponseEntity<List<OrderResponseDto>> getOrdersByDateTimeBetween(
            @RequestParam("before") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam("after") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {

        final List<OrderResponseDto> orders = orderService.getOrdersByCompletedAtBetween(start, end);

        return ResponseEntity.ok(orders);
    }

    @GetMapping
    @Operation(summary = "Get all orders", description = "Retrieves a list of all orders in the system")
    public ResponseEntity<List<OrderResponseDto>> getAllOrders() {

        final List<OrderResponseDto> orders = orderService.getAllOrders();

        return ResponseEntity.ok(orders);
    }

    @GetMapping("/by-venue/{name}")
    @Operation(summary = "Get orders by venue name", description = "Retrieves paginated orders for a specific venue")
    public ResponseEntity<Page<OrderResponseDto>> getOrdersByVenueName(
            @PathVariable String name,
            @ParameterObject Pageable pageable) {

        return ResponseEntity.ok(orderService.getOrdersByVenueName(name, pageable));
    }

    @PatchMapping("/add/{id}")
    @Operation(summary = "Add ticket to order", description = "Adds a ticket to an existing order")
    public ResponseEntity<OrderResponseDto> addTicketToOrder(
            @PathVariable UUID id,
            @Valid @RequestBody TicketRequestDto requestDto) {

        final OrderResponseDto order = orderService.addTicketToOrder(id, requestDto);

        return ResponseEntity.ok(order);
    }

    @DeleteMapping("/{id}/tickets/{ticketId}")
    @Operation(summary = "Remove ticket from order", description = "Removes a ticket from an existing order")
    public ResponseEntity<OrderResponseDto> removeTicketFromOrder(@PathVariable UUID id, @PathVariable UUID ticketId) {

        final OrderResponseDto order = orderService.removeTicketFromOrder(id, ticketId);

        return ResponseEntity.ok(order);
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Update order by ID", description = "Updates order status or completion date")
    public ResponseEntity<OrderResponseDto> updateOrderById(
            @PathVariable UUID id,
            @Valid @RequestBody OrderUpdateDto updateDto) {

        final OrderResponseDto order = orderService.updateOrderById(id, updateDto);

        return ResponseEntity.ok(order);
    }

    @PatchMapping("/delete/{id}")
    @Operation(summary = "Soft delete order by ID", description = "Soft deletes an order (marks as deleted without removing from database)")
    public ResponseEntity<OrderResponseDto> softDeleteOrderById(@PathVariable UUID id) {

        final OrderResponseDto order = orderService.softDeleteOrderById(id);

        return ResponseEntity.ok(order);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Hard delete order by ID", description = "Permanently deletes an order from the database")
    public ResponseEntity<Void> deleteOrderById(@PathVariable UUID id) {

        orderService.deleteOrderById(id);

        return ResponseEntity.noContent().build();
    }
}