import { useState } from 'react';
import { useEvents } from '../hooks/useEvents';
import { useVenues } from '../hooks/useVenues';
import { EventRequest } from '../types/event';
import { Modal } from '../components/Modal';
import { LoadingSpinner } from '../components/LoadingSpinner';
import { ErrorMessage } from '../components/ErrorMessage';
import { Link } from 'react-router-dom';
import { UUID } from '../types/common';
import { VenueSelect } from '../components/EntitySelect';

const VenueNameCell = ({ venueId }: { venueId: UUID }) => {
    const { useVenueById } = useVenues();
    const { data: venue, isLoading } = useVenueById(venueId);
    if (isLoading) return <span>Loading...</span>;
    return (
        <Link to={`/venues/${venueId}`} style={{ color: '#007bff', textDecoration: 'none' }}>
            {venue?.name || venueId.substring(0, 8) + '...'}
        </Link>
    );
};

export const EventsPage = () => {
    const { useAllEvents, useEventByName, useEventsByDateRange, useCreateEvent, useDeleteEvent } = useEvents();
    const { data: events, isLoading, error, refetch } = useAllEvents();
    const createEvent = useCreateEvent();
    const deleteEvent = useDeleteEvent();

    const [isCreateModalOpen, setIsCreateModalOpen] = useState(false);
    const [searchType, setSearchType] = useState<'all' | 'name' | 'date'>('all');
    const [searchName, setSearchName] = useState('');
    const [dateStart, setDateStart] = useState('');
    const [dateEnd, setDateEnd] = useState('');

    const { data: eventByName, refetch: searchByName } = useEventByName(searchName);
    const { data: eventsByDate, refetch: searchByDate } = useEventsByDateRange(dateStart, dateEnd);

    const [formData, setFormData] = useState<EventRequest>({
        name: '',
        description: '',
        dateTime: '',
        venueId: '',
    });

    const displayEvents = (() => {
        if (searchType === 'name' && eventByName) return [eventByName];
        if (searchType === 'date' && eventsByDate) return eventsByDate;
        return events;
    })();

    const handleSearch = () => {
        if (searchType === 'name' && searchName) searchByName();
        if (searchType === 'date' && dateStart && dateEnd) searchByDate();
    };

    const handleCreate = async (e: React.FormEvent) => {
        e.preventDefault();
        try {
            await createEvent.mutateAsync({
                ...formData,
                dateTime: new Date(formData.dateTime).toISOString(),
            });
            setIsCreateModalOpen(false);
            setFormData({ name: '', description: '', dateTime: '', venueId: '' });
        } catch (err: any) {
            const detail = err.response?.data?.detail || '';
            if (detail.includes('Duplicate') || detail.includes('already exists')) {
                alert('Event with this name already exists');
            } else if (detail.includes('Venue') || detail.includes('venue')) {
                alert('Venue not found');
            } else {
                alert(`Error: ${detail}`);
            }
        }
    };

    if (isLoading) return <LoadingSpinner />;
    if (error) return <ErrorMessage message="Error loading events" onRetry={refetch} />;

    return (
        <div>
            <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '20px' }}>
                <h1>Events</h1>
                <button onClick={() => setIsCreateModalOpen(true)} className="btn btn-primary">
                    + Create event
                </button>
            </div>

            <div style={{ display: 'flex', gap: '10px', marginBottom: '20px', alignItems: 'end', flexWrap: 'wrap' }}>
                <div className="form-group" style={{ margin: 0 }}>
                    <label>Search by</label>
                    <select
                        value={searchType}
                        onChange={e => setSearchType(e.target.value as any)}
                        className="form-control"
                    >
                        <option value="all">All</option>
                        <option value="name">Name</option>
                        <option value="date">Date</option>
                    </select>
                </div>

                {searchType === 'name' && (
                    <div className="form-group" style={{ margin: 0 }}>
                        <label>Name</label>
                        <input
                            className="form-control"
                            value={searchName}
                            onChange={e => setSearchName(e.target.value)}
                            placeholder="Enter name"
                        />
                    </div>
                )}

                {searchType === 'date' && (
                    <>
                        <div className="form-group" style={{ margin: 0 }}>
                            <label>From</label>
                            <input
                                type="datetime-local"
                                className="form-control"
                                value={dateStart}
                                onChange={e => setDateStart(e.target.value)}
                            />
                        </div>
                        <div className="form-group" style={{ margin: 0 }}>
                            <label>To</label>
                            <input
                                type="datetime-local"
                                className="form-control"
                                value={dateEnd}
                                onChange={e => setDateEnd(e.target.value)}
                            />
                        </div>
                    </>
                )}

                {searchType !== 'all' && (
                    <button onClick={handleSearch} className="btn btn-primary">Search</button>
                )}
                <button onClick={() => { setSearchType('all'); setSearchName(''); setDateStart(''); setDateEnd(''); }} className="btn">
                    Reset
                </button>
            </div>

            <table>
                <thead>
                <tr>
                    <th>Name</th>
                    <th>Description</th>
                    <th>Date and time</th>
                    <th>Venue</th>
                    <th>Actions</th>
                </tr>
                </thead>
                <tbody>
                {displayEvents?.map(event => (
                    <tr key={event.id}>
                        <td>
                            <Link to={`/events/${event.id}`} style={{ color: '#007bff', textDecoration: 'none' }}>
                                {event.name}
                            </Link>
                        </td>
                        <td>{event.description?.substring(0, 50) || '—'}{event.description?.length > 50 ? '...' : ''}</td>
                        <td>{new Date(event.dateTime).toLocaleString('en-US')}</td>
                        <td><VenueNameCell venueId={event.venueId} /></td>
                        <td>
                            <button
                                onClick={() => deleteEvent.mutate(event.id)}
                                className="btn btn-danger"
                            >
                                Delete
                            </button>
                        </td>
                    </tr>
                ))}
                </tbody>
            </table>

            <Modal isOpen={isCreateModalOpen} onClose={() => setIsCreateModalOpen(false)} title="Create event">
                <form onSubmit={handleCreate}>
                    <div className="form-group">
                        <label>Name *</label>
                        <input
                            className="form-control"
                            value={formData.name}
                            onChange={e => setFormData({ ...formData, name: e.target.value })}
                            required
                            maxLength={200}
                        />
                    </div>
                    <div className="form-group">
                        <label>Description</label>
                        <textarea
                            className="form-control"
                            value={formData.description || ''}
                            onChange={e => setFormData({ ...formData, description: e.target.value })}
                            maxLength={5000}
                        />
                    </div>
                    <div className="form-group">
                        <label>Date and time *</label>
                        <input
                            type="datetime-local"
                            className="form-control"
                            value={formData.dateTime}
                            onChange={e => setFormData({ ...formData, dateTime: e.target.value })}
                            required
                        />
                    </div>
                    <div className="form-group">
                        <label>Venue *</label>
                        <VenueSelect
                            value={formData.venueId}
                            onChange={id => setFormData({ ...formData, venueId: id })}
                            placeholder="Select venue"
                            required
                        />
                    </div>
                    <button type="submit" className="btn btn-primary" style={{ width: '100%' }}>
                        Create
                    </button>
                </form>
            </Modal>
        </div>
    );
};