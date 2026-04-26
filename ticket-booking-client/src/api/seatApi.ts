import apiClient from './client';
import { SeatRequest, SeatResponse, SeatUpdate } from '../types/seat';
import { UUID } from '../types/common';

export const seatApi = {
    create: (data: SeatRequest) =>
        apiClient.post<SeatResponse>('/seats', data).then(res => res.data),

    getById: (id: UUID) =>
        apiClient.get<SeatResponse>(`/seats/${id}`).then(res => res.data),

    getByVenueIdAndNumber: (venueId: UUID, number: number) =>
        apiClient.get<SeatResponse>(`/seats/num/${number}/venue/${venueId}`).then(res => res.data),

    getByVenueIdAndSection: (venueId: UUID, section: number) =>
        apiClient.get<SeatResponse[]>(`/seats/section/${section}/venue/${venueId}`).then(res => res.data),

    getByPriceRange: (min: number, max: number) =>
        apiClient.get<SeatResponse[]>('/seats/by-price', {
            params: { lower: min, higher: max }
        }).then(res => res.data),

    getAll: () =>
        apiClient.get<SeatResponse[]>('/seats').then(res => res.data),

    update: (id: UUID, data: SeatUpdate) =>
        apiClient.patch<SeatResponse>(`/seats/${id}`, data).then(res => res.data),

    delete: (id: UUID) =>
        apiClient.delete(`/seats/${id}`),
};