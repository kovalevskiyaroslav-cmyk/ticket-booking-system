import { useState } from 'react';
import { useParams, Link } from 'react-router-dom';
import { useOrders } from '../hooks/useOrders';
import { useUsers } from '../hooks/useUsers';
import { TicketRequest } from '../types/ticket';
import { LoadingSpinner } from '../components/LoadingSpinner';
import { ErrorMessage } from '../components/ErrorMessage';
import { UUID } from '../types/common';
import { EventSelect, SeatSelect } from '../components/EntitySelect';

export const OrderDetailPage = () => {
    const { id } = useParams<{ id: string }>();
    const { useOrderById, useAddTicketToOrder, useRemoveTicketFromOrder } = useOrders();
    const { useUserById } = useUsers();

    const { data: order, isLoading, error, refetch } = useOrderById(id as UUID);
    const { data: user } = useUserById(order?.userId || null);
    const addTicket = useAddTicketToOrder();
    const removeTicket = useRemoveTicketFromOrder();

    const [ticketForm, setTicketForm] = useState({ seatId: '', eventId: '' });

    const handleAddTicket = async (e: React.FormEvent) => {
        e.preventDefault();
        try {
            await addTicket.mutateAsync({
                orderId: id as UUID,
                ticketData: ticketForm as TicketRequest,
            });
            setTicketForm({ seatId: '', eventId: '' });
        } catch (err: any) {
            const detail = err.response?.data?.detail || '';
            if (detail.includes('Duplicate') || detail.includes('already reserved') || detail.includes('Seat is already')) {
                alert('This seat is already taken for this event');
            } else if (detail.includes('deleted')) {
                alert('Order is deleted and cannot be modified');
            } else if (detail.includes('Event') || detail.includes('event')) {
                alert('Event not found');
            } else if (detail.includes('Seat') || detail.includes('seat')) {
                alert('Seat not found');
            } else if (detail.includes('not found')) {
                alert('Order not found');
            } else {
                alert(`Error: ${detail}`);
            }
        }
    };

    if (isLoading) return <LoadingSpinner />;
    if (error) return <ErrorMessage message="Error loading order" onRetry={refetch} />;
    if (!order) return <ErrorMessage message="Order not found" />;

    return (
        <div>
            <Link to="/orders" style={{ color: '#007bff', textDecoration: 'none', marginBottom: '20px', display: 'block' }}>
                ← Back to orders
            </Link>

            <div style={{ background: 'white', padding: '30px', borderRadius: '8px', boxShadow: '0 2px 4px rgba(0,0,0,0.1)', marginBottom: '30px' }}>
                <h1>Order</h1>
                <div style={{ marginTop: '20px', display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '10px' }}>
                    <p><strong>Status:</strong> {order.status}</p>
                    <p><strong>Total:</strong> {order.totalPrice} ₽</p>
                    <p>
                        <strong>User:</strong>{' '}
                        {user ? (
                            <Link to={`/users/${order.userId}`} style={{ color: '#007bff', textDecoration: 'none' }}>
                                {user.name}
                            </Link>
                        ) : (
                            'Loading...'
                        )}
                    </p>
                    <p><strong>Completed:</strong> {order.completedAt ? new Date(order.completedAt).toLocaleString('en-US') : 'No'}</p>
                    {order.paymentDto && (
                        <p><strong>Payment:</strong> {order.paymentDto.status}</p>
                    )}
                </div>
            </div>

            <div style={{ background: 'white', padding: '30px', borderRadius: '8px', boxShadow: '0 2px 4px rgba(0,0,0,0.1)', marginBottom: '30px' }}>
                <h2>Add ticket</h2>
                <form onSubmit={handleAddTicket} style={{ marginTop: '15px', display: 'flex', gap: '10px' }}>
                    <div className="form-group" style={{ flex: 1, margin: 0 }}>
                        <label>Seat</label>
                        <SeatSelect
                            value={ticketForm.seatId}
                            onChange={id => setTicketForm({ ...ticketForm, seatId: id })}
                            placeholder="Select seat"
                            required
                        />
                    </div>
                    <div className="form-group" style={{ flex: 1, margin: 0 }}>
                        <label>Event</label>
                        <EventSelect
                            value={ticketForm.eventId}
                            onChange={id => setTicketForm({ ...ticketForm, eventId: id })}
                            placeholder="Select event"
                            required
                        />
                    </div>
                    <button type="submit" className="btn btn-primary" style={{ alignSelf: 'end' }}>
                        Add
                    </button>
                </form>
            </div>

            <h2 style={{ marginBottom: '15px' }}>Tickets in order</h2>
            {order.ticketIds.length > 0 ? (
                <table>
                    <thead>
                    <tr>
                        <th>Ticket</th>
                        <th>Actions</th>
                    </tr>
                    </thead>
                    <tbody>
                    {order.ticketIds.map((ticketId, index) => (
                        <tr key={ticketId}>
                            <td>
                                <Link to={`/tickets/${ticketId}`} style={{ color: '#007bff', textDecoration: 'none' }}>
                                    Ticket #{index + 1}
                                </Link>
                            </td>
                            <td>
                                <button
                                    onClick={async () => {
                                        try {
                                            await removeTicket.mutateAsync({ orderId: order.id, ticketId });
                                        } catch (err: any) {
                                            const detail = err.response?.data?.detail || '';
                                            if (detail.includes('deleted')) {
                                                alert('Order is deleted and cannot be modified');
                                            } else if (detail.includes('Ticket') || detail.includes('ticket')) {
                                                alert('Ticket not found in order');
                                            } else if (detail.includes('not found')) {
                                                alert('Order not found');
                                            } else {
                                                alert(`Error: ${detail}`);
                                            }
                                        }
                                    }}
                                    className="btn btn-danger"
                                >
                                    Remove
                                </button>
                            </td>
                        </tr>
                    ))}
                    </tbody>
                </table>
            ) : (
                <p>No tickets in order</p>
            )}
        </div>
    );
};