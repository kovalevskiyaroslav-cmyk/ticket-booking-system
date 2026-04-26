import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { paymentApi } from '../api/paymentApi';
import { PaymentUpdate } from '../types/payment';
import { UUID, PaymentStatus } from '../types/common';

export const usePayments = () => {
    const queryClient = useQueryClient();

    const useAllPayments = () =>
        useQuery({
            queryKey: ['payments'],
            queryFn: paymentApi.getAll,
        });

    const usePaymentById = (id: UUID | null) =>
        useQuery({
            queryKey: ['payments', id],
            queryFn: () => paymentApi.getById(id!),
            enabled: !!id,
        });

    const usePaymentsByStatus = (status: PaymentStatus | null) =>
        useQuery({
            queryKey: ['payments', 'status', status],
            queryFn: () => paymentApi.getByStatus(status!),
            enabled: false,
            retry: false,
        });

    const usePaymentsByAmountRange = (min: number, max: number) =>
        useQuery({
            queryKey: ['payments', 'amountRange', min, max],
            queryFn: () => paymentApi.getByAmountRange(min, max),
            enabled: false,
            retry: false,
        });

    const useUpdatePayment = () =>
        useMutation({
            mutationFn: ({ id, data }: { id: UUID; data: PaymentUpdate }) =>
                paymentApi.update(id, data),
            onSuccess: (_, variables) => {
                queryClient.invalidateQueries({ queryKey: ['payments', variables.id] });
                queryClient.invalidateQueries({ queryKey: ['payments'] });
            },
        });

    return {
        useAllPayments,
        usePaymentById,
        usePaymentsByStatus,
        usePaymentsByAmountRange,
        useUpdatePayment,
    };
};