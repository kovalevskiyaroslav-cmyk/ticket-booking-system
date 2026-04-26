import { UUID } from './common';

export interface SeatRequest {
    number: number;
    section: number;
    price: number;
    venueId: UUID;
}

export interface SeatResponse {
    id: UUID;
    number: number;
    section: number;
    price: number;
    venueId: UUID;
}

export interface SeatUpdate {
    number?: number;
    section?: number;
    price?: number;
}