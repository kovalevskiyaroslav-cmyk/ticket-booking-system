import { useState } from 'react';
import { useParams, Link } from 'react-router-dom';
import { useSeats } from '../hooks/useSeats';
import { useVenues } from '../hooks/useVenues';
import { SeatUpdate } from '../types/seat';
import { LoadingSpinner } from '../components/LoadingSpinner';
import { ErrorMessage } from '../components/ErrorMessage';
import { UUID } from '../types/common';

export const SeatDetailPage = () => {
    const { id } = useParams<{ id: string }>();
    const { useSeatById, useUpdateSeat } = useSeats();
    const { useVenueById } = useVenues();

    const { data: seat, isLoading, error, refetch } = useSeatById(id as UUID);
    const { data: venue } = useVenueById(seat?.venueId || null);
    const updateSeat = useUpdateSeat();

    const [isEditing, setIsEditing] = useState(false);
    const [editForm, setEditForm] = useState<SeatUpdate>({});

    const handleUpdate = async (e: React.FormEvent) => {
        e.preventDefault();

        const updateData: SeatUpdate = {};

        if (editForm.number && editForm.number !== seat?.number) {
            updateData.number = editForm.number;
        }
        if (editForm.section && editForm.section !== seat?.section) {
            updateData.section = editForm.section;
        }
        if (editForm.price && editForm.price !== seat?.price) {
            updateData.price = editForm.price;
        }

        if (Object.keys(updateData).length === 0) {
            setIsEditing(false);
            return;
        }

        try {
            await updateSeat.mutateAsync({
                id: id as UUID,
                data: updateData,
            });
            setIsEditing(false);
        } catch (err: any) {
            const detail = err.response?.data?.detail || '';
            if (detail.includes('Duplicate') || detail.includes('already exists')) {
                alert('This seat already exists in this venue');
            } else if (detail.includes('not found')) {
                alert('Seat not found');
            } else {
                alert(`Error: ${detail}`);
            }
        }
    };

    if (isLoading) return <LoadingSpinner />;
    if (error) return <ErrorMessage message="Error loading seat" onRetry={refetch} />;
    if (!seat) return <ErrorMessage message="Seat not found" />;

    return (
        <div>
            <Link to="/seats" style={{ color: '#007bff', textDecoration: 'none', marginBottom: '20px', display: 'block' }}>
                ← Back to seats
            </Link>

            <div style={{ background: 'white', padding: '30px', borderRadius: '8px', boxShadow: '0 2px 4px rgba(0,0,0,0.1)' }}>
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                    <h1>Seat №{seat.number}</h1>
                    <button
                        onClick={() => {
                            setIsEditing(!isEditing);
                            setEditForm({
                                number: seat.number,
                                section: seat.section,
                                price: seat.price,
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
                            <label>Number</label>
                            <input
                                type="number"
                                className="form-control"
                                value={editForm.number || ''}
                                onChange={e => setEditForm({ ...editForm, number: parseInt(e.target.value) })}
                                min="1"
                            />
                        </div>
                        <div className="form-group">
                            <label>Section</label>
                            <input
                                type="number"
                                className="form-control"
                                value={editForm.section || ''}
                                onChange={e => setEditForm({ ...editForm, section: parseInt(e.target.value) })}
                                min="1"
                            />
                        </div>
                        <div className="form-group">
                            <label>Price</label>
                            <input
                                type="number"
                                step="0.01"
                                className="form-control"
                                value={editForm.price || ''}
                                onChange={e => setEditForm({ ...editForm, price: parseFloat(e.target.value) })}
                                min="0.01"
                            />
                        </div>
                        <button type="submit" className="btn btn-success">
                            {updateSeat.isPending ? 'Saving...' : 'Save'}
                        </button>
                        {updateSeat.isError && (
                            <p className="error-message" style={{ marginTop: '10px' }}>
                                Error updating. Please check the entered data.
                            </p>
                        )}
                    </form>
                ) : (
                    <div style={{ marginTop: '20px' }}>
                        <p><strong>Section:</strong> {seat.section}</p>
                        <p><strong>Price:</strong> {seat.price} ₽</p>
                        <p><strong>Venue:</strong>{' '}
                            {venue ? (
                                <Link to={`/venues/${seat.venueId}`} style={{ color: '#007bff', textDecoration: 'none' }}>
                                    {venue.name}
                                </Link>
                            ) : (
                                'Loading...'
                            )}
                        </p>
                    </div>
                )}
            </div>
        </div>
    );
};