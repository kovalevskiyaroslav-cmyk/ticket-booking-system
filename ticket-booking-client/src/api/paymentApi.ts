import apiClient from './client';
import { PaymentResponse, PaymentUpdate } from '../types/payment';
import { UUID, PaymentStatus } from '../types/common';

export const paymentApi = {
    getById: (id: UUID) =>
        apiClient.get<PaymentResponse>(`/payments/${id}`).then(res => res.data),

    getByAmountRange: (min: number, max: number) =>
        apiClient.get<PaymentResponse[]>('/payments/by-amount-range', {
            params: { min, max }
        }).then(res => res.data),

    getByStatus: (status: PaymentStatus) =>
        apiClient.get<PaymentResponse[]>(`/payments/status/${status}`).then(res => res.data),

    getAll: () =>
        apiClient.get<PaymentResponse[]>('/payments').then(res => res.data),

    update: (id: UUID, data: PaymentUpdate) =>
        apiClient.patch<PaymentResponse>(`/payments/${id}`, data).then(res => res.data),
};