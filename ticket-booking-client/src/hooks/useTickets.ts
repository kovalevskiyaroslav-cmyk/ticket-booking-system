import { useQuery } from '@tanstack/react-query';
import { ticketApi } from '../api/ticketApi';
import { UUID, Pageable } from '../types/common';

export const useTickets = () => {
    const useAllTickets = () =>
        useQuery({
            queryKey: ['tickets'],
            queryFn: ticketApi.getAll,
        });

    const useTicketById = (id: UUID | null) =>
        useQuery({
            queryKey: ['tickets', id],
            queryFn: () => ticketApi.getById(id!),
            enabled: !!id,
        });

    const useTicketsByEventId = (eventId: UUID | null) =>
        useQuery({
            queryKey: ['tickets', 'event', eventId],
            queryFn: () => ticketApi.getByEventId(eventId!),
            enabled: !!eventId,
        });

    const useTicketsByPriceRange = (min: number, max: number) =>
        useQuery({
            queryKey: ['tickets', 'priceRange', min, max],
            queryFn: () => ticketApi.getByPriceRange(min, max),
            enabled: false,
            retry: false,
        });

    const useTicketsByEventName = (name: string, pageable: Pageable) =>
        useQuery({
            queryKey: ['tickets', 'eventName', name, pageable],
            queryFn: () => ticketApi.getByEventName(name, pageable),
            enabled: false,
            retry: false,
        });

    return {
        useAllTickets,
        useTicketById,
        useTicketsByEventId,
        useTicketsByPriceRange,
        useTicketsByEventName,
    };
};