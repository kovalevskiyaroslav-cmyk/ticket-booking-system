import { UUID } from './common';

export interface VenueRequest {
    name: string;
    address: string;
}

export interface VenueResponse {
    id: UUID;
    name: string;
    address: string;
}

export interface VenueUpdate {
    name?: string;
    address?: string;
}