import { UUID, PaymentStatus } from './common';

export interface PaymentResponse {
    id: UUID;
    status: PaymentStatus;
    amount: number;
}

export interface PaymentUpdate {
    status: PaymentStatus;
}