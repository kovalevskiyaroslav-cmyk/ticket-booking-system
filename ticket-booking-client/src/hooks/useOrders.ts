import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { orderApi } from '../api/orderApi';
import { OrderRequest, OrderUpdate } from '../types/order';
import { TicketRequest } from '../types/ticket';
import { UUID, OrderStatus, Pageable } from '../types/common';

export const useOrders = () => {
    const queryClient = useQueryClient();

    const useAllOrders = () =>
        useQuery({
            queryKey: ['orders'],
            queryFn: orderApi.getAll,
        });

    const useOrderById = (id: UUID | null) =>
        useQuery({
            queryKey: ['orders', id],
            queryFn: () => orderApi.getById(id!),
            enabled: !!id,
        });

    const useOrdersByStatus = (status: OrderStatus | null) =>
        useQuery({
            queryKey: ['orders', 'status', status],
            queryFn: () => orderApi.getByStatus(status!),
            enabled: false,
            retry: false,
        });

    const useOrdersByDeleted = (deleted: boolean | null) =>
        useQuery({
            queryKey: ['orders', 'deleted', deleted],
            queryFn: () => orderApi.getByDeleted(deleted!),
            enabled: false,
            retry: false,
        });

    const useOrdersByDateRange = (start: string, end: string) =>
        useQuery({
            queryKey: ['orders', 'dateRange', start, end],
            queryFn: () => orderApi.getByDateRange(start, end),
            enabled: false,
            retry: false,
        });

    const useOrdersByVenueName = (name: string, pageable: Pageable) =>
        useQuery({
            queryKey: ['orders', 'venue', name, pageable],
            queryFn: () => orderApi.getByVenueName(name, pageable),
            enabled: false,
            retry: false,
        });

    const useCreateOrder = () =>
        useMutation({
            mutationFn: (data: OrderRequest) => orderApi.create(data),
            onSuccess: () => {
                queryClient.invalidateQueries({ queryKey: ['orders'] });
            },
        });

    const useCreateOrdersBulk = () =>
        useMutation({
            mutationFn: (data: OrderRequest[]) => orderApi.createBulk(data),
            onSuccess: () => {
                queryClient.invalidateQueries({ queryKey: ['orders'] });
            },
        });

    const useAddTicketToOrder = () =>
        useMutation({
            mutationFn: ({ orderId, ticketData }: { orderId: UUID; ticketData: TicketRequest }) =>
                orderApi.addTicket(orderId, ticketData),
            onSuccess: () => {
                queryClient.invalidateQueries({ queryKey: ['orders'] });
            },
        });

    const useRemoveTicketFromOrder = () =>
        useMutation({
            mutationFn: ({ orderId, ticketId }: { orderId: UUID; ticketId: UUID }) =>
                orderApi.removeTicket(orderId, ticketId),
            onSuccess: () => {
                queryClient.invalidateQueries({ queryKey: ['orders'] });
            },
        });

    const useUpdateOrder = () =>
        useMutation({
            mutationFn: ({ id, data }: { id: UUID; data: OrderUpdate }) =>
                orderApi.update(id, data),
            onSuccess: () => {
                queryClient.invalidateQueries({ queryKey: ['orders'] });
            },
        });

    const useSoftDeleteOrder = () =>
        useMutation({
            mutationFn: (id: UUID) => orderApi.softDelete(id),
            onSuccess: () => {
                queryClient.invalidateQueries({ queryKey: ['orders'] });
            },
        });

    const useHardDeleteOrder = () =>
        useMutation({
            mutationFn: (id: UUID) => orderApi.hardDelete(id),
            onSuccess: () => {
                queryClient.invalidateQueries({ queryKey: ['orders'] });
            },
        });

    return {
        useAllOrders,
        useOrderById,
        useOrdersByStatus,
        useOrdersByDeleted,
        useOrdersByDateRange,
        useOrdersByVenueName,
        useCreateOrder,
        useCreateOrdersBulk,
        useAddTicketToOrder,
        useRemoveTicketFromOrder,
        useUpdateOrder,
        useSoftDeleteOrder,
        useHardDeleteOrder,
    };
};