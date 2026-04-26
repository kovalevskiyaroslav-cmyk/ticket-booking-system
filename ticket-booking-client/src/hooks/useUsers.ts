import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { userApi } from '../api/userApi';
import { UserRequest, UserUpdate } from '../types/user';
import { UUID } from '../types/common';

export const useUsers = () => {
    const queryClient = useQueryClient();

    const useAllUsers = () =>
        useQuery({
            queryKey: ['users'],
            queryFn: userApi.getAll,
        });

    const useUserById = (id: UUID | null) =>
        useQuery({
            queryKey: ['users', id],
            queryFn: () => userApi.getById(id!),
            enabled: !!id,
        });

    const useUserByName = (name: string) =>
        useQuery({
            queryKey: ['users', 'name', name],
            queryFn: () => userApi.getByName(name),
            enabled: false,
            retry: false,
        });

    const useUserByPhone = (phone: string) =>
        useQuery({
            queryKey: ['users', 'phone', phone],
            queryFn: () => userApi.getByPhone(phone),
            enabled: false,
            retry: false,
        });

    const useUserByEmail = (email: string) =>
        useQuery({
            queryKey: ['users', 'email', email],
            queryFn: () => userApi.getByEmail(email),
            enabled: false,
            retry: false,
        });

    const useCreateUser = () =>
        useMutation({
            mutationFn: (data: UserRequest) => userApi.create(data),
            onSuccess: () => {
                queryClient.invalidateQueries({ queryKey: ['users'] });
            },
        });

    const useUpdateUser = () =>
        useMutation({
            mutationFn: ({ id, data }: { id: UUID; data: UserUpdate }) =>
                userApi.update(id, data),
            onSuccess: (_, variables) => {
                // Инвалидируем только конкретного пользователя
                queryClient.invalidateQueries({ queryKey: ['users', variables.id] });
            },
        });

    const useAddFavoriteEvent = () =>
        useMutation({
            mutationFn: ({ userId, eventId }: { userId: UUID; eventId: UUID }) =>
                userApi.addFavoriteEvent(userId, eventId),
            onSuccess: (_, variables) => {
                queryClient.invalidateQueries({ queryKey: ['users', variables.userId] });
            },
        });

    const useRemoveFavoriteEvent = () =>
        useMutation({
            mutationFn: ({ userId, eventId }: { userId: UUID; eventId: UUID }) =>
                userApi.removeFavoriteEvent(userId, eventId),
            onSuccess: (_, variables) => {
                queryClient.invalidateQueries({ queryKey: ['users', variables.userId] });
            },
        });

    const useDeleteUser = () =>
        useMutation({
            mutationFn: (id: UUID) => userApi.delete(id),
            onSuccess: () => {
                queryClient.invalidateQueries({ queryKey: ['users'] });
            },
        });

    return {
        useAllUsers,
        useUserById,
        useUserByName,
        useUserByPhone,
        useUserByEmail,
        useCreateUser,
        useUpdateUser,
        useAddFavoriteEvent,
        useRemoveFavoriteEvent,
        useDeleteUser,
    };
};