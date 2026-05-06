import { useState } from 'react';
import { useParams, Link } from 'react-router-dom';
import { useUsers } from '../hooks/useUsers';
import { useEvents } from '../hooks/useEvents';
import { UserUpdate } from '../types/user';
import { LoadingSpinner } from '../components/LoadingSpinner';
import { ErrorMessage } from '../components/ErrorMessage';
import { UUID } from '../types/common';
import { EventSelect } from '../components/EntitySelect';

const EventNameBadge = ({ eventId }: { eventId: UUID }) => {
    const { useEventById } = useEvents();
    const { data: event, isLoading } = useEventById(eventId);
    if (isLoading) return <span>Loading...</span>;
    return (
        <Link to={`/events/${eventId}`} style={{ color: '#007bff', textDecoration: 'none' }}>
            {event?.name || eventId.substring(0, 8) + '...'}
        </Link>
    );
};

export const UserDetailPage = () => {
    const { id } = useParams<{ id: string }>();
    const {
        useUserById,
        useUpdateUser,
        useAddFavoriteEvent,
        useRemoveFavoriteEvent
    } = useUsers();

    const { data: user, isLoading, error, refetch } = useUserById(id as UUID);
    const updateUser = useUpdateUser();
    const addFavorite = useAddFavoriteEvent();
    const removeFavorite = useRemoveFavoriteEvent();

    const [isEditing, setIsEditing] = useState(false);
    const [editForm, setEditForm] = useState<UserUpdate>({});
    const [favoriteEventId, setFavoriteEventId] = useState('');
    const [errorMsg, setErrorMsg] = useState('');
    const [favoriteError, setFavoriteError] = useState('');

    const handleUpdate = async (e: React.FormEvent) => {
        e.preventDefault();
        setErrorMsg('');

        const updateData: UserUpdate = {};
        if (editForm.name && editForm.name !== user?.name) updateData.name = editForm.name;
        if (editForm.email && editForm.email !== user?.email) updateData.email = editForm.email;
        if (editForm.phone && editForm.phone !== user?.phone) updateData.phone = editForm.phone;

        if (Object.keys(updateData).length === 0) {
            setIsEditing(false);
            return;
        }

        try {
            await updateUser.mutateAsync({ id: id as UUID, data: updateData });
            setIsEditing(false);
            setErrorMsg('');
        } catch (err: any) {
            const detail = err.response?.data?.detail || err.message || '';

            if (detail.includes('Duplicate') && detail.includes('email')) {
                setErrorMsg('User with this email already exists');
            } else if (detail.includes('Duplicate') && detail.includes('phone')) {
                setErrorMsg('User with this phone already exists');
            } else if (detail.includes('not found')) {
                setErrorMsg('User not found');
            } else {
                setErrorMsg(`Error: ${detail}`);
            }
        }
    };

    const handleAddFavorite = async (e: React.FormEvent) => {
        e.preventDefault();
        setFavoriteError('');

        try {
            await addFavorite.mutateAsync({ userId: id as UUID, eventId: favoriteEventId as UUID });
            setFavoriteEventId('');
        } catch (err: any) {
            const detail = err.response?.data?.detail || err.message || '';

            if (detail.includes('already exists') || detail.includes('Favorite')) {
                setFavoriteError('This event is already in favorites');
            } else if (detail.includes('not found')) {
                setFavoriteError('Event not found');
            } else {
                setFavoriteError(`Error: ${detail}`);
            }
        }
    };

    const handleRemoveFavorite = async (eventId: UUID) => {
        setFavoriteError('');

        try {
            await removeFavorite.mutateAsync({ userId: id as UUID, eventId });
        } catch (err: any) {
            const detail = err.response?.data?.detail || err.message || '';

            if (detail.includes('not found')) {
                setFavoriteError('Event not found in favorites');
            } else {
                setFavoriteError(`Error: ${detail}`);
            }
        }
    };

    if (isLoading) return <LoadingSpinner />;
    if (error) return <ErrorMessage message="Error loading user" onRetry={refetch} />;
    if (!user) return <ErrorMessage message="User not found" />;

    return (
        <div>
            <Link to="/users" style={{ color: '#007bff', textDecoration: 'none', marginBottom: '20px', display: 'block' }}>
                ← Back to users
            </Link>

            <div style={{ background: 'white', padding: '30px', borderRadius: '8px', boxShadow: '0 2px 4px rgba(0,0,0,0.1)', marginBottom: '30px' }}>
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                    <h1>{user.name}</h1>
                    <button
                        onClick={() => {
                            setIsEditing(!isEditing);
                            setEditForm({ name: user.name, email: user.email, phone: user.phone });
                            setErrorMsg('');
                        }}
                        className="btn btn-primary"
                    >
                        {isEditing ? 'Cancel' : 'Edit'}
                    </button>
                </div>

                {isEditing ? (
                    <form onSubmit={handleUpdate} style={{ marginTop: '20px' }}>
                        {errorMsg && <ErrorMessage message={errorMsg} />}

                        <div className="form-group">
                            <label>Name</label>
                            <input className="form-control" value={editForm.name || ''} onChange={e => setEditForm({ ...editForm, name: e.target.value })} maxLength={100} />
                        </div>
                        <div className="form-group">
                            <label>Email</label>
                            <input type="email" className="form-control" value={editForm.email || ''} onChange={e => setEditForm({ ...editForm, email: e.target.value })} maxLength={255} />
                        </div>
                        <div className="form-group">
                            <label>Phone</label>
                            <input type="tel" className="form-control" value={editForm.phone || ''} onChange={e => setEditForm({ ...editForm, phone: e.target.value })} pattern="^[+]?[0-9\s\-\(\)]{8,20}$" />
                        </div>
                        <button type="submit" className="btn btn-success">
                            {updateUser.isPending ? 'Saving...' : 'Save'}
                        </button>
                    </form>
                ) : (
                    <div style={{ marginTop: '20px' }}>
                        <p><strong>Email:</strong> {user.email}</p>
                        <p><strong>Phone:</strong> {user.phone}</p>
                        <p><strong>Orders count:</strong> {user.orderIds.length}</p>
                    </div>
                )}
            </div>

            <div style={{ background: 'white', padding: '30px', borderRadius: '8px', boxShadow: '0 2px 4px rgba(0,0,0,0.1)', marginBottom: '30px' }}>
                <h2>Favorite events</h2>

                {favoriteError && <ErrorMessage message={favoriteError} />}

                <form onSubmit={handleAddFavorite} style={{ display: 'flex', gap: '10px', marginTop: '15px', marginBottom: '20px' }}>
                    <div className="form-group" style={{ flex: 1, margin: 0 }}>
                        <EventSelect
                            value={favoriteEventId}
                            onChange={id => setFavoriteEventId(id)}
                            placeholder="Select event"
                            required
                        />
                    </div>
                    <button type="submit" className="btn btn-primary">Add</button>
                </form>

                {user.favoriteEventIds.length > 0 ? (
                    <div style={{ display: 'flex', flexWrap: 'wrap', gap: '10px' }}>
                        {user.favoriteEventIds.map(eventId => (
                            <div key={eventId} style={{ background: '#f0f0f0', padding: '10px', borderRadius: '4px', display: 'flex', alignItems: 'center', gap: '10px' }}>
                                <EventNameBadge eventId={eventId} />
                                <button onClick={() => handleRemoveFavorite(eventId)} className="btn btn-danger" style={{ padding: '2px 8px', fontSize: '12px' }}>×</button>
                            </div>
                        ))}
                    </div>
                ) : (
                    <p>No favorite events</p>
                )}
            </div>

            <div style={{ background: 'white', padding: '30px', borderRadius: '8px', boxShadow: '0 2px 4px rgba(0,0,0,0.1)' }}>
                <h2>User orders</h2>
                {user.orderIds.length > 0 ? (
                    <div style={{ marginTop: '15px', display: 'flex', flexWrap: 'wrap', gap: '10px' }}>
                        {user.orderIds.map((orderId, index) => (
                            <Link key={orderId} to={`/orders/${orderId}`} style={{ background: '#f0f0f0', padding: '10px', borderRadius: '4px', color: '#007bff', textDecoration: 'none' }}>
                                Order #{index + 1}
                            </Link>
                        ))}
                    </div>
                ) : (
                    <p>No orders</p>
                )}
            </div>
        </div>
    );
};