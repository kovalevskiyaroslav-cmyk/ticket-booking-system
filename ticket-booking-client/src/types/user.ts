import { UUID } from './common';

export interface UserRequest {
    name: string;
    email: string;
    phone: string;
}

export interface UserResponse {
    id: UUID;
    name: string;
    email: string;
    phone: string;
    orderIds: UUID[];
    favoriteEventIds: UUID[];
}

export interface UserUpdate {
    name?: string;
    email?: string;
    phone?: string;
}