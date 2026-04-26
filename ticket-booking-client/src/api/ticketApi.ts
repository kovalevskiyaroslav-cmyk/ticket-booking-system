import apiClient from './client';
import { TicketResponse } from '../types/ticket';
import { UUID, Page, Pageable } from '../types/common';

export const ticketApi = {
    getById: (id: UUID) =>
        apiClient.get<TicketResponse>(`/tickets/${id}`).then(res => res.data),

    getByPriceRange: (min: number, max: number) =>
        apiClient.get<TicketResponse[]>('/tickets/by-price', {
            params: { lower: min, higher: max }
        }).then(res => res.data),

    getByEventId: (eventId: UUID) =>
        apiClient.get<TicketResponse[]>(`/tickets/event/${eventId}`).then(res => res.data),

    getAll: () =>
        apiClient.get<TicketResponse[]>('/tickets').then(res => res.data),

    getByEventName: (name: string, pageable?: Pageable) =>
        apiClient.get<Page<TicketResponse>>(`/tickets/by-event/${encodeURIComponent(name)}`, {
            params: pageable || { page: 0, size: 20 }
        }).then(res => res.data),
};