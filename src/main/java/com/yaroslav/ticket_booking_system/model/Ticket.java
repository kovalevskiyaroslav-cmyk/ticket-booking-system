package com.yaroslav.ticket_booking_system.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tickets")
public class Ticket extends AbstractAuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column
    private LocalDateTime purchaseDateTime;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal price;

    @OneToMany(mappedBy = "ticket")
    private List<Seat> seats;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    public Ticket(Order order, Event event) {
        this.order = order;
        this.event = event;
        this.price = BigDecimal.ZERO;
        this.seats = new ArrayList<>();
    }

    public void addSeat(Seat seat) {
        if (!seats.contains(seat)) {
            seats.add(seat);
            seat.setTicket(this);
            price = price.add(seat.getPrice());
        }
    }

    public void removeSeat(Seat seat) {
        seats.remove(seat);
        seat.setTicket(null);
        price = price.subtract(seat.getPrice());
    }
}
