import { useState } from 'react';
import { useParams, Link } from 'react-router-dom';
import { useVenues } from '../hooks/useVenues';
import { VenueUpdate } from '../types/venue';
import { LoadingSpinner } from '../components/LoadingSpinner';
import { ErrorMessage } from '../components/ErrorMessage';
import { UUID } from '../types/common';

export const VenueDetailPage = () => {
    const { id } = useParams<{ id: string }>();
    const { useVenueById, useUpdateVenue } = useVenues();

    const { data: venue, isLoading, error, refetch } = useVenueById(id as UUID);
    const updateVenue = useUpdateVenue();

    const [isEditing, setIsEditing] = useState(false);
    const [editForm, setEditForm] = useState<VenueUpdate>({});

    const handleUpdate = async (e: React.FormEvent) => {
        e.preventDefault();

        const updateData: VenueUpdate = {};

        if (editForm.name && editForm.name !== venue?.name) {
            updateData.name = editForm.name;
        }
        if (editForm.address && editForm.address !== venue?.address) {
            updateData.address = editForm.address;
        }

        if (Object.keys(updateData).length === 0) {
            setIsEditing(false);
            return;
        }

        try {
            await updateVenue.mutateAsync({
                id: id as UUID,
                data: updateData,
            });
            setIsEditing(false);
        } catch (err: any) {
            const detail = err.response?.data?.detail || '';
            if (detail.includes('name') && (detail.includes('Duplicate') || detail.includes('already exists'))) {
                alert('Venue with this name already exists');
            } else if (detail.includes('address') && (detail.includes('Duplicate') || detail.includes('already exists'))) {
                alert('Venue with this address already exists');
            } else if (detail.includes('not found')) {
                alert('Venue not found');
            } else if (detail.includes('Duplicate') || detail.includes('already exists')) {
                alert('This venue already exists');
            } else {
                alert(`Error: ${detail}`);
            }
        }
    };

    if (isLoading) return <LoadingSpinner />;
    if (error) return <ErrorMessage message="Error loading venue" onRetry={refetch} />;
    if (!venue) return <ErrorMessage message="Venue not found" />;

    return (
        <div>
            <Link to="/venues" style={{ color: '#007bff', textDecoration: 'none', marginBottom: '20px', display: 'block' }}>
                ← Back to venues
            </Link>

            <div style={{ background: 'white', padding: '30px', borderRadius: '8px', boxShadow: '0 2px 4px rgba(0,0,0,0.1)' }}>
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                    <h1>{venue.name}</h1>
                    <button
                        onClick={() => {
                            setIsEditing(!isEditing);
                            setEditForm({
                                name: venue.name,
                                address: venue.address,
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
                            <label>Address</label>
                            <input
                                className="form-control"
                                value={editForm.address || ''}
                                onChange={e => setEditForm({ ...editForm, address: e.target.value })}
                                maxLength={300}
                            />
                        </div>
                        <button type="submit" className="btn btn-success">
                            {updateVenue.isPending ? 'Saving...' : 'Save'}
                        </button>
                        {updateVenue.isError && (
                            <p className="error-message" style={{ marginTop: '10px' }}>
                                Error updating. Please check the entered data.
                            </p>
                        )}
                    </form>
                ) : (
                    <div style={{ marginTop: '20px' }}>
                        <p><strong>ID:</strong> {venue.id}</p>
                        <p><strong>Address:</strong> {venue.address}</p>
                    </div>
                )}
            </div>
        </div>
    );
};