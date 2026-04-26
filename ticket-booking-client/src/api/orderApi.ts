import apiClient from './client';
import { OrderRequest, OrderResponse, OrderUpdate } from '../types/order';
import { TicketRequest } from '../types/ticket';
import { UUID, OrderStatus, Page, Pageable } from '../types/common';

export const orderApi = {
    create: (data: OrderRequest) =>
        apiClient.post<OrderResponse>('/orders', data).then(res => res.data),

    createBulk: (data: OrderRequest[]) =>
        apiClient.post<OrderResponse[]>('/orders/bulk', data).then(res => res.data),

    getById: (id: UUID) =>
        apiClient.get<OrderResponse>(`/orders/${id}`).then(res => res.data),

    getByStatus: (status: OrderStatus) =>
        apiClient.get<OrderResponse[]>(`/orders/status/${status}`).then(res => res.data),

    getByDeleted: (deleted: boolean) =>
        apiClient.get<OrderResponse[]>(`/orders/deleted/${deleted}`).then(res => res.data),

    getByDateRange: (start: string, end: string) =>
        apiClient.get<OrderResponse[]>('/orders/by-date', {
            params: { before: start, after: end }
        }).then(res => res.data),

    getAll: () =>
        apiClient.get<OrderResponse[]>('/orders').then(res => res.data),

    getByVenueName: (name: string, pageable?: Pageable) =>
        apiClient.get<Page<OrderResponse>>(`/orders/by-venue/${encodeURIComponent(name)}`, {
            params: pageable || { page: 0, size: 20 }
        }).then(res => res.data),

    addTicket: (id: UUID, ticketData: TicketRequest) =>
        apiClient.patch<OrderResponse>(`/orders/add/${id}`, ticketData).then(res => res.data),

    removeTicket: (orderId: UUID, ticketId: UUID) =>
        apiClient.delete<OrderResponse>(`/orders/${orderId}/tickets/${ticketId}`).then(res => res.data),

    update: (id: UUID, data: OrderUpdate) =>
        apiClient.patch<OrderResponse>(`/orders/${id}`, data).then(res => res.data),

    softDelete: (id: UUID) =>
        apiClient.patch<OrderResponse>(`/orders/delete/${id}`).then(res => res.data),

    hardDelete: (id: UUID) =>
        apiClient.delete(`/orders/${id}`),
};