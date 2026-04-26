import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { venueApi } from '../api/venueApi';
import { VenueRequest, VenueUpdate } from '../types/venue';
import { UUID } from '../types/common';

export const useVenues = () => {
    const queryClient = useQueryClient();

    const useAllVenues = () =>
        useQuery({
            queryKey: ['venues'],
            queryFn: venueApi.getAll,
        });

    const useVenueById = (id: UUID | null) =>
        useQuery({
            queryKey: ['venues', id],
            queryFn: () => venueApi.getById(id!),
            enabled: !!id,
        });

    const useVenueByName = (name: string) =>
        useQuery({
            queryKey: ['venues', 'name', name],
            queryFn: () => venueApi.getByName(name),
            enabled: false,
            retry: false,
        });

    const useVenueByAddress = (address: string) =>
        useQuery({
            queryKey: ['venues', 'address', address],
            queryFn: () => venueApi.getByAddress(address),
            enabled: false,
            retry: false,
        });

    const useCreateVenue = () =>
        useMutation({
            mutationFn: (data: VenueRequest) => venueApi.create(data),
            onSuccess: () => {
                queryClient.invalidateQueries({ queryKey: ['venues'] });
            },
        });

    const useUpdateVenue = () =>
        useMutation({
            mutationFn: ({ id, data }: { id: UUID; data: VenueUpdate }) =>
                venueApi.update(id, data),
            onSuccess: (_, variables) => {
                queryClient.invalidateQueries({ queryKey: ['venues', variables.id] });
                queryClient.invalidateQueries({ queryKey: ['venues'] });
            },
        });

    const useDeleteVenue = () =>
        useMutation({
            mutationFn: (id: UUID) => venueApi.delete(id),
            onSuccess: () => {
                queryClient.invalidateQueries({ queryKey: ['venues'] });
            },
        });

    return {
        useAllVenues,
        useVenueById,
        useVenueByName,
        useVenueByAddress,
        useCreateVenue,
        useUpdateVenue,
        useDeleteVenue,
    };
};