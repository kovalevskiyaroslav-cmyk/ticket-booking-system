import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { eventApi } from '../api/eventApi';
import { EventRequest, EventUpdate } from '../types/event';
import { UUID } from '../types/common';

export const useEvents = () => {
    const queryClient = useQueryClient();

    const useAllEvents = () =>
        useQuery({
            queryKey: ['events'],
            queryFn: eventApi.getAll,
        });

    const useEventById = (id: UUID | null) =>
        useQuery({
            queryKey: ['events', id],
            queryFn: () => eventApi.getById(id!),
            enabled: !!id,
        });

    const useEventByName = (name: string) =>
        useQuery({
            queryKey: ['events', 'name', name],
            queryFn: () => eventApi.getByName(name),
            enabled: false,
            retry: false, // ← Отключаем повторные попытки
        });

    const useEventsByDateRange = (start: string, end: string) =>
        useQuery({
            queryKey: ['events', 'dateRange', start, end],
            queryFn: () => eventApi.getByDateRange(start, end),
            enabled: false,
            retry: false, // ← Отключаем повторные попытки
        });

    const useCreateEvent = () =>
        useMutation({
            mutationFn: (data: EventRequest) => eventApi.create(data),
            onSuccess: () => {
                queryClient.invalidateQueries({ queryKey: ['events'] });
            },
        });

    const useUpdateEvent = () =>
        useMutation({
            mutationFn: ({ id, data }: { id: UUID; data: EventUpdate }) =>
                eventApi.update(id, data),
            onSuccess: (_, variables) => {
                queryClient.invalidateQueries({ queryKey: ['events', variables.id] });
                queryClient.invalidateQueries({ queryKey: ['events'] });
            },
        });

    const useDeleteEvent = () =>
        useMutation({
            mutationFn: (id: UUID) => eventApi.delete(id),
            onSuccess: () => {
                queryClient.invalidateQueries({ queryKey: ['events'] });
            },
        });

    return {
        useAllEvents,
        useEventById,
        useEventByName,
        useEventsByDateRange,
        useCreateEvent,
        useUpdateEvent,
        useDeleteEvent,
    };
};