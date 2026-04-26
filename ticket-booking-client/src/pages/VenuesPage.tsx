import { useState } from 'react';
import { useVenues } from '../hooks/useVenues';
import { VenueRequest } from '../types/venue';
import { Modal } from '../components/Modal';
import { LoadingSpinner } from '../components/LoadingSpinner';
import { ErrorMessage } from '../components/ErrorMessage';
import { Link } from 'react-router-dom';

export const VenuesPage = () => {
    const { useAllVenues, useVenueByName, useVenueByAddress, useCreateVenue, useDeleteVenue } = useVenues();
    const { data: venues, isLoading, error, refetch } = useAllVenues();
    const createVenue = useCreateVenue();
    const deleteVenue = useDeleteVenue();

    const [isCreateModalOpen, setIsCreateModalOpen] = useState(false);
    const [searchType, setSearchType] = useState<'all' | 'name' | 'address'>('all');
    const [searchValue, setSearchValue] = useState('');
    const [formData, setFormData] = useState<VenueRequest>({ name: '', address: '' });

    const { data: venueByName, refetch: searchByName } = useVenueByName(searchValue);
    const { data: venueByAddress, refetch: searchByAddress } = useVenueByAddress(searchValue);

    const displayVenues = (() => {
        if (searchType === 'name' && venueByName) return [venueByName];
        if (searchType === 'address' && venueByAddress) return [venueByAddress];
        return venues;
    })();

    const handleSearch = () => {
        if (searchType === 'name' && searchValue) searchByName();
        if (searchType === 'address' && searchValue) searchByAddress();
    };

    const handleCreate = async (e: React.FormEvent) => {
        e.preventDefault();
        try {
            await createVenue.mutateAsync(formData);
            setIsCreateModalOpen(false);
            setFormData({ name: '', address: '' });
        } catch (err: any) {
            const detail = err.response?.data?.detail || '';
            if (detail.includes('name') && (detail.includes('Duplicate') || detail.includes('already exists'))) {
                alert('Venue with this name already exists');
            } else if (detail.includes('address') && (detail.includes('Duplicate') || detail.includes('already exists'))) {
                alert('Venue with this address already exists');
            } else if (detail.includes('Duplicate') || detail.includes('already exists')) {
                alert('This venue already exists');
            } else {
                alert(`Error: ${detail}`);
            }
        }
    };

    if (isLoading) return <LoadingSpinner />;
    if (error) return <ErrorMessage message="Error loading venues" onRetry={refetch} />;

    return (
        <div>
            <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '20px' }}>
                <h1>Venues</h1>
                <button onClick={() => setIsCreateModalOpen(true)} className="btn btn-primary">
                    + Create venue
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
                        <option value="address">Address</option>
                    </select>
                </div>

                {searchType !== 'all' && (
                    <div className="form-group" style={{ margin: 0 }}>
                        <label>{searchType === 'name' ? 'Name' : 'Address'}</label>
                        <input
                            className="form-control"
                            value={searchValue}
                            onChange={e => setSearchValue(e.target.value)}
                            placeholder={searchType === 'name' ? 'Enter name' : 'Enter address'}
                        />
                    </div>
                )}

                {searchType !== 'all' && (
                    <button onClick={handleSearch} className="btn btn-primary">Search</button>
                )}
                <button onClick={() => { setSearchType('all'); setSearchValue(''); }} className="btn">
                    Reset
                </button>
            </div>

            <table>
                <thead>
                <tr>
                    <th>Name</th>
                    <th>Address</th>
                    <th>Actions</th>
                </tr>
                </thead>
                <tbody>
                {displayVenues?.map(venue => (
                    <tr key={venue.id}>
                        <td>
                            <Link to={`/venues/${venue.id}`} style={{ color: '#007bff', textDecoration: 'none' }}>
                                {venue.name}
                            </Link>
                        </td>
                        <td>{venue.address}</td>
                        <td>
                            <button onClick={() => deleteVenue.mutate(venue.id)} className="btn btn-danger">
                                Delete
                            </button>
                        </td>
                    </tr>
                ))}
                </tbody>
            </table>

            <Modal isOpen={isCreateModalOpen} onClose={() => setIsCreateModalOpen(false)} title="Create venue">
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
                        <label>Address *</label>
                        <input
                            className="form-control"
                            value={formData.address}
                            onChange={e => setFormData({ ...formData, address: e.target.value })}
                            required
                            maxLength={300}
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