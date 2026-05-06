import { useVenues } from '../hooks/useVenues';
import { useEvents } from '../hooks/useEvents';
import { useUsers } from '../hooks/useUsers';
import { useSeats } from '../hooks/useSeats';
import { UUID } from '../types/common';

interface EntitySelectProps {
    value: string;
    onChange: (id: UUID) => void;
    placeholder?: string;
    required?: boolean;
}

export const VenueSelect = ({ value, onChange, placeholder, required }: EntitySelectProps) => {
    const { useAllVenues } = useVenues();
    const { data: venues, isLoading } = useAllVenues();

    return (
        <select
            className="form-control"
            value={value}
            onChange={e => onChange(e.target.value as UUID)}
            required={required}
        >
            <option value="">{isLoading ? 'Loading...' : placeholder || 'Select venue...'}</option>
            {venues?.map(venue => (
                <option key={venue.id} value={venue.id}>
                    {venue.name} ({venue.address})
                </option>
            ))}
        </select>
    );
};

export const EventSelect = ({ value, onChange, placeholder, required }: EntitySelectProps) => {
    const { useAllEvents } = useEvents();
    const { data: events, isLoading } = useAllEvents();

    return (
        <select
            className="form-control"
            value={value}
            onChange={e => onChange(e.target.value as UUID)}
            required={required}
        >
            <option value="">{isLoading ? 'Loading...' : placeholder || 'Select event...'}</option>
            {events?.map(event => (
                <option key={event.id} value={event.id}>
                    {event.name}
                </option>
            ))}
        </select>
    );
};

export const UserSelect = ({ value, onChange, placeholder, required }: EntitySelectProps) => {
    const { useAllUsers } = useUsers();
    const { data: users, isLoading } = useAllUsers();

    return (
        <select
            className="form-control"
            value={value}
            onChange={e => onChange(e.target.value as UUID)}
            required={required}
        >
            <option value="">{isLoading ? 'Loading...' : placeholder || 'Select user...'}</option>
            {users?.map(user => (
                <option key={user.id} value={user.id}>
                    {user.name} ({user.email})
                </option>
            ))}
        </select>
    );
};

export const SeatSelect = ({ value, onChange, placeholder, required }: EntitySelectProps) => {
    const { useAllSeats } = useSeats();
    const { data: seats, isLoading } = useAllSeats();

    return (
        <select
            className="form-control"
            value={value}
            onChange={e => onChange(e.target.value as UUID)}
            required={required}
        >
            <option value="">{isLoading ? 'Loading...' : placeholder || 'Select seat...'}</option>
            {seats?.map(seat => (
                <option key={seat.id} value={seat.id}>
                    Seat №{seat.number} (Section {seat.section}) — {seat.price} ₽
                </option>
            ))}
        </select>
    );
};