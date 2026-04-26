import apiClient from './client';
import { VenueRequest, VenueResponse, VenueUpdate } from '../types/venue';
import { UUID } from '../types/common';

export const venueApi = {
    create: (data: VenueRequest) =>
        apiClient.post<VenueResponse>('/venues', data).then(res => res.data),

    getById: (id: UUID) =>
        apiClient.get<VenueResponse>(`/venues/${id}`).then(res => res.data),

    getByName: (name: string) =>
        apiClient.get<VenueResponse>(`/venues/name/${encodeURIComponent(name)}`).then(res => res.data),

    getByAddress: (address: string) =>
        apiClient.get<VenueResponse>(`/venues/address/${encodeURIComponent(address)}`).then(res => res.data),

    getAll: () =>
        apiClient.get<VenueResponse[]>('/venues').then(res => res.data),

    update: (id: UUID, data: VenueUpdate) =>
        apiClient.patch<VenueResponse>(`/venues/${id}`, data).then(res => res.data),

    delete: (id: UUID) =>
        apiClient.delete(`/venues/${id}`),
};