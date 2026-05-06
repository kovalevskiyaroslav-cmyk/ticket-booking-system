import { useState } from 'react';
import { useSeats } from '../hooks/useSeats';
import { useVenues } from '../hooks/useVenues';
import { SeatRequest } from '../types/seat';
import { Modal } from '../components/Modal';
import { LoadingSpinner } from '../components/LoadingSpinner';
import { ErrorMessage } from '../components/ErrorMessage';
import { Link } from 'react-router-dom';
import { UUID } from '../types/common';
import { VenueSelect } from '../components/EntitySelect';

const VenueName = ({ venueId }: { venueId: UUID }) => {
    const { useVenueById } = useVenues();
    const { data: venue, isLoading } = useVenueById(venueId);

    if (isLoading) return <span>Loading...</span>;
    return (
        <Link to={`/venues/${venueId}`} style={{ color: '#007bff', textDecoration: 'none' }}>
            {venue?.name || venueId}
        </Link>
    );
};

export const SeatsPage = () => {
    const { useAllSeats, useSeatByVenueAndNumber, useSeatsByVenueAndSection, useSeatsByPriceRange, useCreateSeat, useDeleteSeat } = useSeats();
    const { data: seats, isLoading, error, refetch } = useAllSeats();
    const createSeat = useCreateSeat();
    const deleteSeat = useDeleteSeat();

    const [isCreateModalOpen, setIsCreateModalOpen] = useState(false);
    const [searchType, setSearchType] = useState<'all' | 'venueNumber' | 'venueSection' | 'price'>('all');
    const [venueId, setVenueId] = useState('');
    const [seatNumber, setSeatNumber] = useState('');
    const [section, setSection] = useState('');
    const [minPrice, setMinPrice] = useState('');
    const [maxPrice, setMaxPrice] = useState('');

    const { data: seatByNumber, refetch: searchByNumber } = useSeatByVenueAndNumber(
        venueId as UUID || null,
        parseInt(seatNumber) || null
    );
    const { data: seatsBySection, refetch: searchBySection } = useSeatsByVenueAndSection(
        venueId as UUID || null,
        parseInt(section) || null
    );
    const { data: seatsByPrice, refetch: searchByPrice } = useSeatsByPriceRange(
        parseFloat(minPrice) || 0,
        parseFloat(maxPrice) || 0
    );

    const [formData, setFormData] = useState({
        number: '',
        section: '',
        price: '',
        venueId: '',
    });

    const displaySeats = (() => {
        if (searchType === 'venueNumber' && seatByNumber) return [seatByNumber];
        if (searchType === 'venueSection' && seatsBySection) return seatsBySection;
        if (searchType === 'price' && seatsByPrice) return seatsByPrice;
        return seats;
    })();

    const handleSearch = () => {
        if (searchType === 'venueNumber' && venueId && seatNumber) searchByNumber();
        if (searchType === 'venueSection' && venueId && section) searchBySection();
        if (searchType === 'price' && minPrice && maxPrice) searchByPrice();
    };

    const handleCreate = async (e: React.FormEvent) => {
        e.preventDefault();
        try {
            await createSeat.mutateAsync({
                number: parseInt(formData.number),
                section: parseInt(formData.section),
                price: parseFloat(formData.price),
                venueId: formData.venueId,
            } as SeatRequest);
            setIsCreateModalOpen(false);
            setFormData({ number: '', section: '', price: '', venueId: '' });
        } catch (err: any) {
            const detail = err.response?.data?.detail || '';
            if (detail.includes('Duplicate') || detail.includes('already exists')) {
                alert('This seat already exists in this venue');
            } else if (detail.includes('Venue') || detail.includes('venue')) {
                alert('Venue not found');
            } else {
                alert(`Error: ${detail}`);
            }
        }
    };

    if (isLoading) return <LoadingSpinner />;
    if (error) return <ErrorMessage message="Error loading seats" onRetry={refetch} />;

    return (
        <div>
            <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '20px' }}>
                <h1>Seats</h1>
                <button onClick={() => setIsCreateModalOpen(true)} className="btn btn-primary">
                    + Create seat
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
                        <option value="venueNumber">Venue and number</option>
                        <option value="venueSection">Venue and section</option>
                        <option value="price">Price range</option>
                    </select>
                </div>

                {(searchType === 'venueNumber' || searchType === 'venueSection') && (
                    <div className="form-group" style={{ margin: 0 }}>
                        <label>Venue</label>
                        <VenueSelect
                            value={venueId}
                            onChange={id => setVenueId(id)}
                            placeholder="Select venue"
                        />
                    </div>
                )}

                {searchType === 'venueNumber' && (
                    <div className="form-group" style={{ margin: 0 }}>
                        <label>Seat number</label>
                        <input
                            type="number"
                            className="form-control"
                            value={seatNumber}
                            onChange={e => setSeatNumber(e.target.value)}
                            placeholder="1"
                        />
                    </div>
                )}

                {searchType === 'venueSection' && (
                    <div className="form-group" style={{ margin: 0 }}>
                        <label>Section</label>
                        <input
                            type="number"
                            className="form-control"
                            value={section}
                            onChange={e => setSection(e.target.value)}
                            placeholder="1"
                        />
                    </div>
                )}

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

                {searchType !== 'all' && (
                    <button onClick={handleSearch} className="btn btn-primary">Search</button>
                )}
                <button onClick={() => { setSearchType('all'); setVenueId(''); setSeatNumber(''); setSection(''); setMinPrice(''); setMaxPrice(''); }} className="btn">
                    Reset
                </button>
            </div>

            <table>
                <thead>
                <tr>
                    <th>Number</th>
                    <th>Section</th>
                    <th>Price</th>
                    <th>Venue</th>
                    <th>Actions</th>
                </tr>
                </thead>
                <tbody>
                {displaySeats?.map(seat => (
                    <tr key={seat.id}>
                        <td>
                            <Link to={`/seats/${seat.id}`} style={{ color: '#007bff', textDecoration: 'none' }}>
                                {seat.number}
                            </Link>
                        </td>
                        <td>{seat.section}</td>
                        <td>{seat.price} ₽</td>
                        <td><VenueName venueId={seat.venueId} /></td>
                        <td>
                            <button onClick={() => deleteSeat.mutate(seat.id)} className="btn btn-danger">
                                Delete
                            </button>
                        </td>
                    </tr>
                ))}
                </tbody>
            </table>

            <Modal isOpen={isCreateModalOpen} onClose={() => setIsCreateModalOpen(false)} title="Create seat">
                <form onSubmit={handleCreate}>
                    <div className="form-group">
                        <label>Number *</label>
                        <input
                            type="number"
                            className="form-control"
                            value={formData.number}
                            onChange={e => setFormData({ ...formData, number: e.target.value })}
                            required
                            min="1"
                        />
                    </div>
                    <div className="form-group">
                        <label>Section *</label>
                        <input
                            type="number"
                            className="form-control"
                            value={formData.section}
                            onChange={e => setFormData({ ...formData, section: e.target.value })}
                            required
                            min="1"
                        />
                    </div>
                    <div className="form-group">
                        <label>Price *</label>
                        <input
                            type="number"
                            step="0.01"
                            className="form-control"
                            value={formData.price}
                            onChange={e => setFormData({ ...formData, price: e.target.value })}
                            required
                            min="0.01"
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