import { useParams, Link } from 'react-router-dom';
import { useTickets } from '../hooks/useTickets';
import { useEvents } from '../hooks/useEvents';
import { useSeats } from '../hooks/useSeats';
import { LoadingSpinner } from '../components/LoadingSpinner';
import { ErrorMessage } from '../components/ErrorMessage';
import { UUID } from '../types/common';

export const TicketDetailPage = () => {
    const { id } = useParams<{ id: string }>();
    const { useTicketById } = useTickets();
    const { useEventById } = useEvents();
    const { useSeatById } = useSeats();

    const { data: ticket, isLoading, error, refetch } = useTicketById(id as UUID);
    const { data: event } = useEventById(ticket?.eventId || null);
    const { data: seat } = useSeatById(ticket?.seatId || null);

    if (isLoading) return <LoadingSpinner />;
    if (error) return <ErrorMessage message="Error loading ticket" onRetry={refetch} />;
    if (!ticket) return <ErrorMessage message="Ticket not found" />;

    return (
        <div>
            <Link to="/tickets" style={{ color: '#007bff', textDecoration: 'none', marginBottom: '20px', display: 'block' }}>
                ← Back to tickets
            </Link>

            <div style={{ background: 'white', padding: '30px', borderRadius: '8px', boxShadow: '0 2px 4px rgba(0,0,0,0.1)' }}>
                <h1>Ticket</h1>

                <div style={{
                    marginTop: '20px',
                    display: 'grid',
                    gridTemplateColumns: '1fr',
                    gap: '15px',
                    background: '#f8f9fa',
                    padding: '20px',
                    borderRadius: '8px'
                }}>
                    <div>
                        <p style={{ color: '#666', fontSize: '14px' }}>Price</p>
                        <p style={{ fontWeight: '500', color: '#28a745', fontSize: '20px', margin: 0 }}>{ticket.price} ₽</p>
                    </div>
                </div>

                <div style={{
                    marginTop: '20px',
                    display: 'grid',
                    gridTemplateColumns: '1fr 1fr 1fr',
                    gap: '15px'
                }}>
                    <div style={{
                        background: '#f0f8ff',
                        padding: '15px',
                        borderRadius: '8px',
                        border: '1px solid #b8daff'
                    }}>
                        <p style={{ color: '#004085', fontSize: '13px', marginBottom: '5px' }}>Event</p>
                        <Link to={`/events/${ticket.eventId}`} style={{ color: '#007bff', fontWeight: '500', textDecoration: 'none' }}>
                            {event?.name || 'Loading...'}
                        </Link>
                    </div>

                    <div style={{
                        background: '#f0f8ff',
                        padding: '15px',
                        borderRadius: '8px',
                        border: '1px solid #b8daff'
                    }}>
                        <p style={{ color: '#004085', fontSize: '13px', marginBottom: '5px' }}>Seat</p>
                        <Link to={`/seats/${ticket.seatId}`} style={{ color: '#007bff', fontWeight: '500', textDecoration: 'none' }}>
                            {seat ? `№${seat.number}` : 'Loading...'}
                        </Link>
                    </div>

                    <div style={{
                        background: '#f0f8ff',
                        padding: '15px',
                        borderRadius: '8px',
                        border: '1px solid #b8daff'
                    }}>
                        <p style={{ color: '#004085', fontSize: '13px', marginBottom: '5px' }}>Order</p>
                        <Link to={`/orders/${ticket.orderId}`} style={{ color: '#007bff', fontWeight: '500', textDecoration: 'none' }}>
                            View order
                        </Link>
                    </div>
                </div>
            </div>
        </div>
    );
};