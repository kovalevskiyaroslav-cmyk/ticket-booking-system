package com.yaroslav.ticket_booking_system.controller;

import com.yaroslav.ticket_booking_system.dto.OrderRequestDto;
import com.yaroslav.ticket_booking_system.dto.OrderResponseDto;
import com.yaroslav.ticket_booking_system.dto.OrderUpdateDto;
import com.yaroslav.ticket_booking_system.dto.TicketRequestDto;
import com.yaroslav.ticket_booking_system.model.OrderStatus;
import com.yaroslav.ticket_booking_system.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderResponseDto> createOrder(@RequestBody OrderRequestDto requestDto) {

        final OrderResponseDto created = orderService.createOrder(requestDto);

        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponseDto> getOrderById(@PathVariable UUID id) {

        final OrderResponseDto order = orderService.getOrderById(id);

        return ResponseEntity.ok(order);
    }

    @GetMapping("/status/{orderStatus}")
    public ResponseEntity<List<OrderResponseDto>> getOrdersByStatus(@PathVariable OrderStatus orderStatus) {

        final List<OrderResponseDto> orders = orderService.getOrdersByStatus(orderStatus);

        return ResponseEntity.ok(orders);
    }

    @GetMapping("/deleted/{deleted}")
    public ResponseEntity<List<OrderResponseDto>> getOrdersByDeleted(@PathVariable Boolean deleted) {

        final List<OrderResponseDto> orders = orderService.getOrdersByDeleted(deleted);

        return ResponseEntity.ok(orders);
    }

    @GetMapping("/by-date")
    public ResponseEntity<List<OrderResponseDto>> getOrdersByDateTimeBetween(
            @RequestParam("before") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam("after") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {

        final List<OrderResponseDto> orders = orderService.getOrdersByCompletedAtBetween(start, end);

        return ResponseEntity.ok(orders);
    }

    @GetMapping
    public ResponseEntity<List<OrderResponseDto>> getAllOrders() {

        final List<OrderResponseDto> orders = orderService.getAllOrders();

        return ResponseEntity.ok(orders);
    }

    @GetMapping("/by-venue/{venueId}")
    public ResponseEntity<Page<OrderResponseDto>> getOrdersByVenue(
            @PathVariable UUID venueId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        final Pageable pageable = PageRequest.of(page, size);

        return ResponseEntity.ok(orderService.getOrdersByVenue(venueId, pageable));
    }

    @PatchMapping("/add/{id}")
    public ResponseEntity<OrderResponseDto> addTicketToOrder(
            @PathVariable UUID id,
            @RequestBody TicketRequestDto requestDto) {

        final OrderResponseDto order = orderService.addTicketToOrder(id, requestDto);

        return ResponseEntity.ok(order);
    }

    @DeleteMapping("/{id}/tickets/{ticketId}")
    public ResponseEntity<OrderResponseDto> removeTicketFromOrder(@PathVariable UUID id, @PathVariable UUID ticketId) {

        final OrderResponseDto order = orderService.removeTicketFromOrder(id, ticketId);

        return ResponseEntity.ok(order);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<OrderResponseDto> updateOrderById(@PathVariable UUID id, @RequestBody OrderUpdateDto updateDto) {

        final OrderResponseDto order = orderService.updateOrderById(id, updateDto);

        return ResponseEntity.ok(order);
    }

    @PatchMapping("/delete/{id}")
    public ResponseEntity<OrderResponseDto> softDeleteOrderById(@PathVariable UUID id) {

        final OrderResponseDto order = orderService.softDeleteOrderById(id);

        return ResponseEntity.ok(order);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrderById(@PathVariable UUID id) {

        orderService.deleteOrderById(id);

        return ResponseEntity.noContent().build();
    }
}
