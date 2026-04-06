package com.yaroslav.ticket_booking_system.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(VenueNotFoundException.class)
    public ProblemDetail handleVenueNotFound(VenueNotFoundException ex) {
        return clientError(HttpStatus.NOT_FOUND, "Venue Not Found", ex.getMessage());
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ProblemDetail handleUserNotFound(UserNotFoundException ex) {
        return clientError(HttpStatus.NOT_FOUND, "User Not Found", ex.getMessage());
    }

    @ExceptionHandler(TicketNotFoundException.class)
    public ProblemDetail handleTicketNotFound(TicketNotFoundException ex) {
        return clientError(HttpStatus.NOT_FOUND, "Ticket Not Found", ex.getMessage());
    }

    @ExceptionHandler(SeatNotFoundException.class)
    public ProblemDetail handleSeatNotFound(SeatNotFoundException ex) {
        return clientError(HttpStatus.NOT_FOUND, "Seat Not Found", ex.getMessage());
    }

    @ExceptionHandler(PaymentNotFoundException.class)
    public ProblemDetail handlePaymentNotFound(PaymentNotFoundException ex) {
        return clientError(HttpStatus.NOT_FOUND, "Payment Not Found", ex.getMessage());
    }

    @ExceptionHandler(OrderNotFoundException.class)
    public ProblemDetail handleOrderNotFound(OrderNotFoundException ex) {
        return clientError(HttpStatus.NOT_FOUND, "Order Not Found", ex.getMessage());
    }

    @ExceptionHandler(EventNotFoundException.class)
    public ProblemDetail handleEventNotFound(EventNotFoundException ex) {
        return clientError(HttpStatus.NOT_FOUND, "Event Not Found", ex.getMessage());
    }

    @ExceptionHandler(DuplicateEmailException.class)
    public ProblemDetail handleDuplicateEmail(DuplicateEmailException ex) {
        return clientError(HttpStatus.CONFLICT, "Duplicate Email", ex.getMessage());
    }

    @ExceptionHandler(DuplicatePhoneException.class)
    public ProblemDetail handleDuplicatePhone(DuplicatePhoneException ex) {
        return clientError(HttpStatus.CONFLICT, "Duplicate Phone", ex.getMessage());
    }

    @ExceptionHandler(InvalidOrderStatusTransitionException.class)
    public ProblemDetail handleInvalidOrderStatusTransition(InvalidOrderStatusTransitionException ex) {
        return clientError(HttpStatus.BAD_REQUEST, "Invalid Order Status Transition", ex.getMessage());
    }

    @ExceptionHandler(OrderAlreadyDeletedException.class)
    public ProblemDetail handleOrderAlreadyDeleted(OrderAlreadyDeletedException ex) {
        return clientError(HttpStatus.CONFLICT, "Order Already Deleted", ex.getMessage());
    }

    @ExceptionHandler(IllegalStateException.class)
    public ProblemDetail handleIllegalState(IllegalStateException ex) {
        return clientError(HttpStatus.CONFLICT, "Operation Conflict", ex.getMessage());
    }

    @ExceptionHandler(DuplicateEventNameException.class)
    public ProblemDetail handleDuplicateEventName(DuplicateEventNameException ex) {
        return clientError(HttpStatus.CONFLICT, "Duplicate Event Name", ex.getMessage());
    }

    @ExceptionHandler(InvalidPaymentStatusTransitionException.class)
    public ProblemDetail handleInvalidPaymentStatusTransition(InvalidPaymentStatusTransitionException ex) {
        return clientError(HttpStatus.BAD_REQUEST, "Invalid Payment Status Transition", ex.getMessage());
    }

    @ExceptionHandler({FavoriteEventAlreadyExistsException.class, FavoriteEventNotFoundException.class})
    public ProblemDetail handleFavoriteEventExceptions(RuntimeException ex) {
        return clientError(HttpStatus.BAD_REQUEST, "Favorite Event Operation Failed", ex.getMessage());
    }

    @ExceptionHandler({DuplicateVenueNameException.class, DuplicateVenueAddressException.class})
    public ProblemDetail handleDuplicateVenueExceptions(RuntimeException ex) {
        return clientError(HttpStatus.CONFLICT, "Duplicate Venue Information", ex.getMessage());
    }

    @ExceptionHandler(DuplicateTicketException.class)
    public ProblemDetail handleDuplicateTicket(DuplicateTicketException ex) {
        return clientError(HttpStatus.CONFLICT, "Ticket Already Exists", ex.getMessage());
    }

    @ExceptionHandler(DuplicateSeatException.class)
    public ProblemDetail handleDuplicateSeat(DuplicateSeatException ex) {
        return clientError(HttpStatus.CONFLICT, "Seat Already Exists", ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGenericException(Exception ex) {
        log.error("Internal server error", ex); // stacktrace ONLY here

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexpected error occurred"
        );
        problemDetail.setTitle("Internal Server Error");
        problemDetail.setProperty("timestamp", LocalDateTime.now());
        return problemDetail;
    }

    private ProblemDetail clientError(HttpStatus status, String title, String detail) {
        log.warn("{}: {}", title, detail); // no stacktrace

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, detail);
        problemDetail.setTitle(title);
        problemDetail.setProperty("timestamp", LocalDateTime.now());
        return problemDetail;
    }
}