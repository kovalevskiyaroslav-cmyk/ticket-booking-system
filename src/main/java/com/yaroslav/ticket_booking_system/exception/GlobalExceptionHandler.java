package com.yaroslav.ticket_booking_system.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(VenueNotFoundException.class)
    public ProblemDetail handleVenueNotFound(VenueNotFoundException ex) {

        final ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.NOT_FOUND,
                ex.getMessage()
        );
        problemDetail.setTitle("Venue Not Found");

        return problemDetail;
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ProblemDetail handleUserNotFound(UserNotFoundException ex) {

        final ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.NOT_FOUND,
                ex.getMessage()
        );
        problemDetail.setTitle("User Not Found");

        return problemDetail;
    }

    @ExceptionHandler(TicketNotFoundException.class)
    public ProblemDetail handleTicketNotFound(TicketNotFoundException ex) {

        final ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.NOT_FOUND,
                ex.getMessage()
        );
        problemDetail.setTitle("Ticket Not Found");

        return problemDetail;
    }

    @ExceptionHandler(SeatNotFoundException.class)
    public ProblemDetail handleSeatNotFound(SeatNotFoundException ex) {

        final ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.NOT_FOUND,
                ex.getMessage()
        );
        problemDetail.setTitle("Seat Not Found");

        return problemDetail;
    }

    @ExceptionHandler(PaymentNotFoundException.class)
    public ProblemDetail handlePaymentNotFound(PaymentNotFoundException ex) {

        final ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.NOT_FOUND,
                ex.getMessage()
        );
        problemDetail.setTitle("Payment Not Found");

        return problemDetail;
    }

    @ExceptionHandler(OrderNotFoundException.class)
    public ProblemDetail handleOrderNotFound(OrderNotFoundException ex) {

        final ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.NOT_FOUND,
                ex.getMessage()
        );
        problemDetail.setTitle("Order Not Found");

        return problemDetail;
    }

    @ExceptionHandler(EventNotFoundException.class)
    public ProblemDetail handleEventNotFound(EventNotFoundException ex) {

        final ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.NOT_FOUND,
                ex.getMessage()
        );
        problemDetail.setTitle("Event Not Found");

        return problemDetail;
    }

    @ExceptionHandler(DuplicateEmailException.class)
    public ProblemDetail handleDuplicateEmail(DuplicateEmailException ex) {

        final ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.CONFLICT,
                ex.getMessage()
        );
        problemDetail.setTitle("Duplicate Email");

        return problemDetail;
    }

    @ExceptionHandler(DuplicatePhoneException.class)
    public ProblemDetail handleDuplicatePhone(DuplicatePhoneException ex) {

        final ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.CONFLICT,
                ex.getMessage()
        );
        problemDetail.setTitle("Duplicate Phone");

        return problemDetail;
    }

    @ExceptionHandler(InvalidOrderStatusTransitionException.class)
    public ProblemDetail handleInvalidOrderStatusTransition(InvalidOrderStatusTransitionException ex) {

        final ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                ex.getMessage()
        );
        problemDetail.setTitle("Invalid Order Status Transition");

        return problemDetail;
    }

    @ExceptionHandler(OrderAlreadyDeletedException.class)
    public ProblemDetail handleOrderAlreadyDeleted(OrderAlreadyDeletedException ex) {

        final ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.CONFLICT,
                ex.getMessage()
        );
        problemDetail.setTitle("Order Already Deleted");

        return problemDetail;
    }

    @ExceptionHandler(IllegalStateException.class)
    public ProblemDetail handleIllegalState(IllegalStateException ex) {

        final ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.CONFLICT,
                ex.getMessage()
        );
        problemDetail.setTitle("Operation Conflict");

        return problemDetail;
    }

    @ExceptionHandler(DuplicateEventNameException.class)
    public ProblemDetail handleDuplicateEventName(DuplicateEventNameException ex) {

        final ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.CONFLICT,
                ex.getMessage()
        );
        problemDetail.setTitle("Duplicate Event Name");

        return problemDetail;
    }

    @ExceptionHandler(InvalidPaymentStatusTransitionException.class)
    public ProblemDetail handleInvalidPaymentStatusTransition(InvalidPaymentStatusTransitionException ex) {

        final ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                ex.getMessage()
        );
        problemDetail.setTitle("Invalid Payment Status Transition");

        return problemDetail;
    }

    @ExceptionHandler({FavoriteEventAlreadyExistsException.class, FavoriteEventNotFoundException.class})
    public ProblemDetail handleFavoriteEventExceptions(RuntimeException ex) {

        final ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                ex.getMessage()
        );
        problemDetail.setTitle("Favorite Event Operation Failed");

        return problemDetail;
    }

    @ExceptionHandler({DuplicateVenueNameException.class, DuplicateVenueAddressException.class})
    public ProblemDetail handleDuplicateVenueExceptions(RuntimeException ex) {

        final ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.CONFLICT,
                ex.getMessage()
        );
        problemDetail.setTitle("Duplicate Venue Information");

        return problemDetail;
    }

    @ExceptionHandler(DuplicateTicketException.class)
    public ProblemDetail handleDuplicateTicket(DuplicateTicketException ex) {

        final ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.CONFLICT,
                ex.getMessage()
        );
        problemDetail.setTitle("Ticket Already Exists");

        return problemDetail;
    }

    @ExceptionHandler(DuplicateSeatException.class)
    public ProblemDetail handleDuplicateSeat(DuplicateSeatException ex) {

        final ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.CONFLICT,
                ex.getMessage()
        );
        problemDetail.setTitle("Seat Already Exists");

        return problemDetail;
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGenericException() {

        final ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexpected error occurred"
        );
        problemDetail.setTitle("Internal Server Error");
        problemDetail.setProperty("timestamp", LocalDateTime.now());

        return problemDetail;
    }
}
