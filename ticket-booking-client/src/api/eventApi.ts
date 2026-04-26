import apiClient from './client';
import { EventRequest, EventResponse, EventUpdate } from '../types/event';
import { UUID } from '../types/common';

export const eventApi = {
    create: (data: EventRequest) =>
        apiClient.post<EventResponse>('/events', data).then(res => res.data),

    getById: (id: UUID) =>
        apiClient.get<EventResponse>(`/events/${id}`).then(res => res.data),

    getByName: (name: string) =>
        apiClient.get<EventResponse>(`/events/name/${encodeURIComponent(name)}`).then(res => res.data),

    getByDateRange: (start: string, end: string) =>
        apiClient.get<EventResponse[]>('/events/by-date', {
            params: { before: start, after: end }
        }).then(res => res.data),

    getAll: () =>
        apiClient.get<EventResponse[]>('/events').then(res => res.data),

    update: (id: UUID, data: EventUpdate) =>
        apiClient.patch<EventResponse>(`/events/${id}`, data).then(res => res.data),

    delete: (id: UUID) =>
        apiClient.delete(`/events/${id}`),
};