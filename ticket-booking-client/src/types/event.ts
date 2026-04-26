import { UUID } from './common';

export interface EventRequest {
    name: string;
    description?: string;
    dateTime: string;
    venueId: UUID;
}

export interface EventResponse {
    id: UUID;
    name: string;
    description: string;
    dateTime: string;
    venueId: UUID;
}

export interface EventUpdate {
    name?: string;
    description?: string;
    dateTime?: string;
    venueId?: UUID;
}