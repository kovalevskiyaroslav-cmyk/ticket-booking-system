import apiClient from './client';
import { UserRequest, UserResponse, UserUpdate } from '../types/user';
import { UUID } from '../types/common';

export const userApi = {
    create: (data: UserRequest) =>
        apiClient.post<UserResponse>('/users', data).then(res => res.data),

    getById: (id: UUID) =>
        apiClient.get<UserResponse>(`/users/${id}`).then(res => res.data),

    getByName: (name: string) =>
        apiClient.get<UserResponse>(`/users/name/${encodeURIComponent(name)}`).then(res => res.data),

    getByPhone: (phone: string) =>
        apiClient.get<UserResponse>(`/users/phone/${encodeURIComponent(phone)}`).then(res => res.data),

    getByEmail: (email: string) =>
        apiClient.get<UserResponse>(`/users/email/${encodeURIComponent(email)}`).then(res => res.data),

    getAll: () =>
        apiClient.get<UserResponse[]>('/users').then(res => res.data),

    addFavoriteEvent: (userId: UUID, eventId: UUID) =>
        apiClient.patch<UserResponse>(`/users/${userId}/events/${eventId}`).then(res => res.data),

    removeFavoriteEvent: (userId: UUID, eventId: UUID) =>
        apiClient.delete<UserResponse>(`/users/${userId}/events/${eventId}`).then(res => res.data),

    update: (id: UUID, data: UserUpdate) =>
        apiClient.patch<UserResponse>(`/users/${id}`, data).then(res => res.data),

    delete: (id: UUID) =>
        apiClient.delete(`/users/${id}`),
};