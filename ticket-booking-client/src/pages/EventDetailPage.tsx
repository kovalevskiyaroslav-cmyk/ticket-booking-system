import { useState } from 'react';
import { useParams, Link } from 'react-router-dom';
import { useEvents } from '../hooks/useEvents';
import { useTickets } from '../hooks/useTickets';
import { EventUpdate } from '../types/event';
import { LoadingSpinner } from '../components/LoadingSpinner';
import { ErrorMessage } from '../components/ErrorMessage';
import { UUID } from '../types/common';

export const EventDetailPage = () => {
    const { id } = useParams<{ id: string }>();
    const { useEventById, useUpdateEvent } = useEvents();
    const { useTicketsByEventId } = useTickets();

    const { data: event, isLoading, error, refetch } = useEventById(id as UUID);
    const { data: tickets } = useTicketsByEventId(id as UUID);
    const updateEvent = useUpdateEvent();

    const [isEditing, setIsEditing] = useState(false);
    const [editForm, setEditForm] = useState<EventUpdate>({});

    const handleUpdate = async (e: React.FormEvent) => {
        e.preventDefault();

        const updateData: EventUpdate = {};

        if (editForm.name && editForm.name !== event?.name) {
            updateData.name = editForm.name;
        }
        if (editForm.description !== undefined && editForm.description !== event?.description) {
            updateData.description = editForm.description;
        }
        if (editForm.dateTime && editForm.dateTime !== event?.dateTime) {
            updateData.dateTime = editForm.dateTime;
        }
        if (editForm.venueId && editForm.venueId !== event?.venueId) {
            updateData.venueId = editForm.venueId;
        }

        if (Object.keys(updateData).length === 0) {
            setIsEditing(false);
            return;
        }

        try {
            await updateEvent.mutateAsync({
                id: id as UUID,
                data: updateData,
            });
            setIsEditing(false);
        } catch (err: any) {
            const detail = err.response?.data?.detail || '';
            if (detail.includes('Duplicate') || detail.includes('already exists')) {
                alert('Event with this name already exists');
            } else if (detail.includes('Venue') || detail.includes('venue')) {
                alert('Venue not found');
            } else if (detail.includes('not found')) {
                alert('Event not found');
            } else {
                alert(`Error: ${detail}`);
            }
        }
    };

    if (isLoading) return <LoadingSpinner />;
    if (error) return <ErrorMessage message="Error loading event" onRetry={refetch} />;
    if (!event) return <ErrorMessage message="Event not found" />;

    return (
        <div>
            <Link to="/events" style={{ color: '#007bff', textDecoration: 'none', marginBottom: '20px', display: 'block' }}>
                ← Back to events
            </Link>

            <div style={{ background: 'white', padding: '30px', borderRadius: '8px', boxShadow: '0 2px 4px rgba(0,0,0,0.1)' }}>
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                    <h1>{event.name}</h1>
                    <button
                        onClick={() => {
                            setIsEditing(!isEditing);
                            setEditForm({
                                name: event.name,
                                description: event.description || '',
                                dateTime: event.dateTime,
                                venueId: event.venueId,
                            });
                        }}
                        className="btn btn-primary"
                    >
                        {isEditing ? 'Cancel' : 'Edit'}
                    </button>
                </div>

                {isEditing ? (
                    <form onSubmit={handleUpdate} style={{ marginTop: '20px' }}>
                        <div className="form-group">
                            <label>Name</label>
                            <input
                                className="form-control"
                                value={editForm.name || ''}
                                onChange={e => setEditForm({ ...editForm, name: e.target.value })}
                                maxLength={200}
                            />
                        </div>
                        <div className="form-group">
                            <label>Description</label>
                            <textarea
                                className="form-control"
                                value={editForm.description || ''}
                                onChange={e => setEditForm({ ...editForm, description: e.target.value })}
                                maxLength={5000}
                            />
                        </div>
                        <div className="form-group">
                            <label>Date and time</label>
                            <input
                                type="datetime-local"
                                className="form-control"
                                value={editForm.dateTime ? editForm.dateTime.substring(0, 16) : ''}
                                onChange={e => {
                                    const localDateTime = e.target.value;
                                    if (localDateTime) {
                                        const isoDateTime = new Date(localDateTime).toISOString();
                                        setEditForm({ ...editForm, dateTime: isoDateTime });
                                    } else {
                                        setEditForm({ ...editForm, dateTime: undefined });
                                    }
                                }}
                            />
                        </div>
                        <div className="form-group">
                            <label>Venue ID</label>
                            <input
                                className="form-control"
                                value={editForm.venueId || ''}
                                onChange={e => setEditForm({ ...editForm, venueId: e.target.value as UUID })}
                                placeholder="Venue UUID"
                            />
                        </div>
                        <button type="submit" className="btn btn-success">
                            {updateEvent.isPending ? 'Saving...' : 'Save'}
                        </button>
                        {updateEvent.isError && (
                            <p className="error-message" style={{ marginTop: '10px' }}>
                                Error updating. Please check the entered data.
                            </p>
                        )}
                    </form>
                ) : (
                    <div style={{ marginTop: '20px' }}>
                        <p><strong>ID:</strong> {event.id}</p>
                        <p><strong>Description:</strong> {event.description || 'No description'}</p>
                        <p><strong>Date and time:</strong> {new Date(event.dateTime).toLocaleString('en-US')}</p>
                        <p><strong>Venue:</strong> <Link to={`/venues/${event.venueId}`}>{event.venueId}</Link></p>
                    </div>
                )}
            </div>

            <h2 style={{ marginTop: '30px', marginBottom: '15px' }}>Event tickets</h2>
            {tickets && tickets.length > 0 ? (
                <table>
                    <thead>
                    <tr>
                        <th>Ticket ID</th>
                        <th>Price</th>
                        <th>Seat</th>
                    </tr>
                    </thead>
                    <tbody>
                    {tickets.map(ticket => (
                        <tr key={ticket.id}>
                            <td>{ticket.id}</td>
                            <td>{ticket.price} ₽</td>
                            <td>{ticket.seatId}</td>
                        </tr>
                    ))}
                    </tbody>
                </table>
            ) : (
                <p>No tickets available</p>
            )}
        </div>
    );
};