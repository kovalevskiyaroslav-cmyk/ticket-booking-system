import { UUID } from './common';

export interface TicketRequest {
    seatId: UUID;
    eventId: UUID;
}

export interface TicketResponse {
    id: UUID;
    price: number;
    seatId: UUID;
    eventId: UUID;
    orderId: UUID;
}