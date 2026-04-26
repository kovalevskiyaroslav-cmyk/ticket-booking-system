import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { seatApi } from '../api/seatApi';
import { SeatRequest, SeatUpdate } from '../types/seat';
import { UUID } from '../types/common';

export const useSeats = () => {
    const queryClient = useQueryClient();

    const useAllSeats = () =>
        useQuery({
            queryKey: ['seats'],
            queryFn: seatApi.getAll,
        });

    const useSeatById = (id: UUID | null) =>
        useQuery({
            queryKey: ['seats', id],
            queryFn: () => seatApi.getById(id!),
            enabled: !!id,
        });

    const useSeatsByVenueAndSection = (venueId: UUID | null, section: number | null) =>
        useQuery({
            queryKey: ['seats', 'venue', venueId, 'section', section],
            queryFn: () => seatApi.getByVenueIdAndSection(venueId!, section!),
            enabled: false,
            retry: false,
        });

    const useSeatByVenueAndNumber = (venueId: UUID | null, number: number | null) =>
        useQuery({
            queryKey: ['seats', 'venue', venueId, 'number', number],
            queryFn: () => seatApi.getByVenueIdAndNumber(venueId!, number!),
            enabled: false,
            retry: false,
        });

    const useSeatsByPriceRange = (min: number, max: number) =>
        useQuery({
            queryKey: ['seats', 'priceRange', min, max],
            queryFn: () => seatApi.getByPriceRange(min, max),
            enabled: false,
            retry: false,
        });

    const useCreateSeat = () =>
        useMutation({
            mutationFn: (data: SeatRequest) => seatApi.create(data),
            onSuccess: () => {
                queryClient.invalidateQueries({ queryKey: ['seats'] });
            },
        });

    const useUpdateSeat = () =>
        useMutation({
            mutationFn: ({ id, data }: { id: UUID; data: SeatUpdate }) =>
                seatApi.update(id, data),
            onSuccess: (_, variables) => {
                queryClient.invalidateQueries({ queryKey: ['seats', variables.id] });
                queryClient.invalidateQueries({ queryKey: ['seats'] });
            },
        });

    const useDeleteSeat = () =>
        useMutation({
            mutationFn: (id: UUID) => seatApi.delete(id),
            onSuccess: () => {
                queryClient.invalidateQueries({ queryKey: ['seats'] });
            },
        });

    return {
        useAllSeats,
        useSeatById,
        useSeatsByVenueAndSection,
        useSeatByVenueAndNumber,
        useSeatsByPriceRange,
        useCreateSeat,
        useUpdateSeat,
        useDeleteSeat,
    };
};