-- liquibase formatted sql

-- changeset Yaroslav:create-venues
CREATE TABLE venues(
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(100) NOT NULL,
    address VARCHAR(100) NOT NULL,
    city VARCHAR(50) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
-- rollback DROP TABLE venues;

-- changeset Yaroslav:create-users
CREATE TABLE users(
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    phone VARCHAR(20) NOT NULL,
    active BOOLEAN NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
-- rollback DROP TABLE users;

-- changeset Yaroslav:create-payments
CREATE TABLE payments(
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    status VARCHAR(20) NOT NULL,
    payment_amount NUMERIC(19, 2) NOT NULL,
    timestamp TIMESTAMP WITH TIME ZONE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
-- rollback DROP TABLE payments;

-- changeset Yaroslav:create-events
CREATE TABLE events(
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    venue_id UUID NOT NULL,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    date_time TIMESTAMP NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_events_venues FOREIGN KEY (venue_id) REFERENCES venues(id)
);
-- rollback DROP TABLE events;

-- changeset Yaroslav:create-orders
CREATE TABLE orders(
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL,
    payment_id UUID,
    date_time TIMESTAMP NOT NULL,
    status VARCHAR(20) NOT NULL,
    total_price NUMERIC(19, 2) NOT NULL,
    deleted BOOLEAN NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_orders_users FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_orders_payments FOREIGN KEY (payment_id) REFERENCES payments(id)
);
-- rollback DROP TABLE orders;

-- changeset Yaroslav:create-tickets
CREATE TABLE tickets(
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    event_id UUID NOT NULL,
    order_id UUID NOT NULL,
    purchase_date_time TIMESTAMP,
    price NUMERIC(19, 2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_tickets_events FOREIGN KEY (event_id) REFERENCES events(id),
    CONSTRAINT fk_tickets_orders FOREIGN KEY (order_id) REFERENCES orders(id)
);
-- rollback DROP TABLE tickets;

-- changeset Yaroslav:create-seats
CREATE TABLE seats(
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    ticket_id UUID,
    venue_id UUID NOT NULL,
    seat_num INT NOT NULL,
    section INT NOT NULL,
    price NUMERIC(19, 2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_seats_tickets FOREIGN KEY (ticket_id) REFERENCES tickets(id),
    CONSTRAINT fk_seats_venues FOREIGN KEY (venue_id) REFERENCES venues(id)
);
-- rollback DROP TABLE seats;

-- changeset Yaroslav:create-user-favorite-events
CREATE TABLE user_favorite_events(
    user_id UUID NOT NULL,
    event_id UUID NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_user_favorite_events PRIMARY KEY (user_id, event_id),
    CONSTRAINT fk_ufe_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_ufe_event FOREIGN KEY (event_id) REFERENCES events(id)
);
-- rollback DROP TABLE user_favorite_events;