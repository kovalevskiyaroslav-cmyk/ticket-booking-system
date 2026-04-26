import { UUID, OrderStatus } from './common';
import { TicketRequest } from './ticket';
import { PaymentResponse } from './payment';

export interface OrderRequest {
    userId: UUID;
    ticketDtos?: TicketRequest[];
}

export interface OrderResponse {
    id: UUID;
    completedAt: string | null;
    status: OrderStatus;
    totalPrice: number;
    userId: UUID;
    paymentDto: PaymentResponse | null;
    ticketIds: UUID[];
}

export interface OrderUpdate {
    status?: OrderStatus;
    completedAt?: string;
}