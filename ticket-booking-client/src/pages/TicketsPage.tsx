import { useState } from 'react';
import { useTickets } from '../hooks/useTickets';
import { useSeats } from '../hooks/useSeats';
import { useEvents } from '../hooks/useEvents';
import { LoadingSpinner } from '../components/LoadingSpinner';
import { ErrorMessage } from '../components/ErrorMessage';
import { Link } from 'react-router-dom';
import { UUID } from '../types/common';

const SeatNumber = ({ seatId }: { seatId: UUID }) => {
    const { useSeatById } = useSeats();
    const { data: seat, isLoading } = useSeatById(seatId);

    if (isLoading) return <span>Loading...</span>;
    return (
        <Link to={`/seats/${seatId}`} style={{ color: '#007bff', textDecoration: 'none' }}>
            {seat ? `№${seat.number}` : seatId.substring(0, 8) + '...'}
        </Link>
    );
};

const EventName = ({ eventId }: { eventId: UUID }) => {
    const { useEventById } = useEvents();
    const { data: event, isLoading } = useEventById(eventId);

    if (isLoading) return <span>Loading...</span>;
    return (
        <Link to={`/events/${eventId}`} style={{ color: '#007bff', textDecoration: 'none' }}>
            {event?.name || eventId.substring(0, 8) + '...'}
        </Link>
    );
};

export const TicketsPage = () => {
    const { useAllTickets, useTicketsByPriceRange, useTicketsByEventName } = useTickets();
    const { data: tickets, isLoading, error, refetch } = useAllTickets();

    const [searchType, setSearchType] = useState<'all' | 'price' | 'eventName'>('all');
    const [minPrice, setMinPrice] = useState('');
    const [maxPrice, setMaxPrice] = useState('');
    const [eventName, setEventName] = useState('');

    const { data: filteredByPrice, refetch: searchByPrice } = useTicketsByPriceRange(
        parseFloat(minPrice),
        parseFloat(maxPrice)
    );

    const { data: filteredByEvent, refetch: searchByEvent } = useTicketsByEventName(
        eventName,
        { page: 0, size: 20 }
    );

    const displayTickets = (() => {
        if (searchType === 'price' && filteredByPrice) return filteredByPrice;
        if (searchType === 'eventName' && filteredByEvent) return filteredByEvent.content;
        return tickets;
    })();

    const handleSearch = () => {
        if (searchType === 'price' && minPrice && maxPrice) searchByPrice();
        if (searchType === 'eventName' && eventName) searchByEvent();
    };

    if (isLoading) return <LoadingSpinner />;
    if (error) return <ErrorMessage message="Error loading tickets" onRetry={refetch} />;

    return (
        <div>
            <h1 style={{ marginBottom: '20px' }}>Tickets</h1>

            <div style={{ display: 'flex', gap: '10px', marginBottom: '20px', alignItems: 'end', flexWrap: 'wrap' }}>
                <div className="form-group" style={{ margin: 0 }}>
                    <label>Search by</label>
                    <select
                        value={searchType}
                        onChange={e => setSearchType(e.target.value as any)}
                        className="form-control"
                    >
                        <option value="all">All</option>
                        <option value="price">Price</option>
                        <option value="eventName">Event name</option>
                    </select>
                </div>

                {searchType === 'price' && (
                    <>
                        <div className="form-group" style={{ margin: 0 }}>
                            <label>Min price</label>
                            <input
                                type="number"
                                className="form-control"
                                value={minPrice}
                                onChange={e => setMinPrice(e.target.value)}
                                placeholder="0"
                            />
                        </div>
                        <div className="form-group" style={{ margin: 0 }}>
                            <label>Max price</label>
                            <input
                                type="number"
                                className="form-control"
                                value={maxPrice}
                                onChange={e => setMaxPrice(e.target.value)}
                                placeholder="10000"
                            />
                        </div>
                    </>
                )}

                {searchType === 'eventName' && (
                    <div className="form-group" style={{ margin: 0 }}>
                        <label>Event name</label>
                        <input
                            className="form-control"
                            value={eventName}
                            onChange={e => setEventName(e.target.value)}
                            placeholder="Enter name"
                        />
                    </div>
                )}

                {searchType !== 'all' && (
                    <button onClick={handleSearch} className="btn btn-primary">Search</button>
                )}
                <button onClick={() => { setSearchType('all'); setMinPrice(''); setMaxPrice(''); setEventName(''); }} className="btn">
                    Reset
                </button>
            </div>

            {displayTickets && displayTickets.length > 0 ? (
                <table>
                    <thead>
                    <tr>
                        <th>Price</th>
                        <th>Seat</th>
                        <th>Event</th>
                        <th>Actions</th>
                    </tr>
                    </thead>
                    <tbody>
                    {displayTickets.map(ticket => (
                        <tr key={ticket.id}>
                            <td>{ticket.price} ₽</td>
                            <td><SeatNumber seatId={ticket.seatId} /></td>
                            <td><EventName eventId={ticket.eventId} /></td>
                            <td>
                                <Link
                                    to={`/tickets/${ticket.id}`}
                                    className="btn btn-primary"
                                    style={{ padding: '4px 8px', fontSize: '13px', textDecoration: 'none', display: 'inline-block' }}
                                >
                                    View
                                </Link>
                            </td>
                        </tr>
                    ))}
                    </tbody>
                </table>
            ) : (
                <p>No tickets found</p>
            )}
        </div>
    );
};