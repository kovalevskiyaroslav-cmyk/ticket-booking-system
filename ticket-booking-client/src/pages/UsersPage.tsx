import { useState } from 'react';
import { useUsers } from '../hooks/useUsers';
import { UserRequest } from '../types/user';
import { Modal } from '../components/Modal';
import { LoadingSpinner } from '../components/LoadingSpinner';
import { ErrorMessage } from '../components/ErrorMessage';
import { Link } from 'react-router-dom';

export const UsersPage = () => {
    const { useAllUsers, useUserByName, useUserByEmail, useUserByPhone, useCreateUser, useDeleteUser } = useUsers();
    const { data: users, isLoading, error, refetch } = useAllUsers();
    const createUser = useCreateUser();
    const deleteUser = useDeleteUser();

    const [isCreateModalOpen, setIsCreateModalOpen] = useState(false);
    const [searchType, setSearchType] = useState<'all' | 'name' | 'email' | 'phone'>('all');
    const [searchValue, setSearchValue] = useState('');
    const [searchPerformed, setSearchPerformed] = useState(false);
    const [formData, setFormData] = useState<UserRequest>({
        name: '',
        email: '',
        phone: '',
    });

    const { data: userByName, refetch: searchByName, isError: nameError } = useUserByName(searchValue);
    const { data: userByEmail, refetch: searchByEmail, isError: emailError } = useUserByEmail(searchValue);
    const { data: userByPhone, refetch: searchByPhone, isError: phoneError } = useUserByPhone(searchValue);

    const isSearchError = searchPerformed && (
        (searchType === 'name' && nameError) ||
        (searchType === 'email' && emailError) ||
        (searchType === 'phone' && phoneError)
    );

    const displayUsers = (() => {
        if (!searchPerformed) return users;
        if (searchType === 'name' && userByName) return [userByName];
        if (searchType === 'email' && userByEmail) return [userByEmail];
        if (searchType === 'phone' && userByPhone) return [userByPhone];
        return [];
    })();

    const handleSearch = () => {
        setSearchPerformed(true);
        if (searchType === 'name' && searchValue) searchByName();
        if (searchType === 'email' && searchValue) searchByEmail();
        if (searchType === 'phone' && searchValue) searchByPhone();
    };

    const handleReset = () => {
        setSearchType('all');
        setSearchValue('');
        setSearchPerformed(false);
    };

    const handleCreate = async (e: React.FormEvent) => {
        e.preventDefault();
        try {
            await createUser.mutateAsync(formData);
            setIsCreateModalOpen(false);
            setFormData({ name: '', email: '', phone: '' });
        } catch (err: any) {
            console.error('Failed to create user:', err);
            const detail = err.response?.data?.detail || err.message;
            if (detail.includes('Duplicate') || detail.includes('already exists')) {
                alert(`Error: ${detail}`);
            } else {
                alert(`Error creating user: ${detail}`);
            }
        }
    };

    if (isLoading) return <LoadingSpinner />;
    if (error) return <ErrorMessage message="Error loading users" onRetry={refetch} />;

    return (
        <div>
            <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '20px' }}>
                <h1>Users</h1>
                <button onClick={() => setIsCreateModalOpen(true)} className="btn btn-primary">
                    + Create user
                </button>
            </div>

            <div style={{ display: 'flex', gap: '10px', marginBottom: '20px', alignItems: 'end' }}>
                <div className="form-group" style={{ margin: 0 }}>
                    <label>Search by</label>
                    <select
                        value={searchType}
                        onChange={e => setSearchType(e.target.value as any)}
                        className="form-control"
                    >
                        <option value="all">All</option>
                        <option value="name">Name</option>
                        <option value="email">Email</option>
                        <option value="phone">Phone</option>
                    </select>
                </div>
                {searchType !== 'all' && (
                    <>
                        <div className="form-group" style={{ margin: 0 }}>
                            <label>Value</label>
                            <input
                                className="form-control"
                                value={searchValue}
                                onChange={e => setSearchValue(e.target.value)}
                                placeholder={searchType === 'email' ? 'email@example.com' : searchType === 'phone' ? '+79001234567' : 'Name'}
                            />
                        </div>
                        <button onClick={handleSearch} className="btn btn-primary">Search</button>
                    </>
                )}
                <button onClick={handleReset} className="btn">
                    Reset
                </button>
            </div>

            {isSearchError && (
                <ErrorMessage message={`User with ${searchType === 'name' ? 'name' : searchType === 'email' ? 'email' : 'phone'} "${searchValue}" not found`} />
            )}

            {searchPerformed && !isSearchError && displayUsers?.length === 0 && (
                <p style={{ color: '#666', fontStyle: 'italic', marginBottom: '20px' }}>Search returned no results</p>
            )}

            <table>
                <thead>
                <tr>
                    <th>Name</th>
                    <th>Email</th>
                    <th>Phone</th>
                    <th>Orders</th>
                    <th>Favorite events</th>
                    <th>Actions</th>
                </tr>
                </thead>
                <tbody>
                {displayUsers?.map(user => (
                    <tr key={user.id}>
                        <td>
                            <Link to={`/users/${user.id}`} style={{ color: '#007bff', textDecoration: 'none' }}>
                                {user.name}
                            </Link>
                        </td>
                        <td>{user.email}</td>
                        <td>{user.phone}</td>
                        <td>{user.orderIds.length}</td>
                        <td>{user.favoriteEventIds.length}</td>
                        <td>
                            <button
                                onClick={() => deleteUser.mutate(user.id)}
                                className="btn btn-danger"
                            >
                                Delete
                            </button>
                        </td>
                    </tr>
                ))}
                </tbody>
            </table>

            <Modal isOpen={isCreateModalOpen} onClose={() => setIsCreateModalOpen(false)} title="Create user">
                <form onSubmit={handleCreate}>
                    <div className="form-group">
                        <label>Name *</label>
                        <input
                            className="form-control"
                            value={formData.name}
                            onChange={e => setFormData({ ...formData, name: e.target.value })}
                            required
                            maxLength={100}
                        />
                    </div>
                    <div className="form-group">
                        <label>Email *</label>
                        <input
                            type="email"
                            className="form-control"
                            value={formData.email}
                            onChange={e => setFormData({ ...formData, email: e.target.value })}
                            required
                            maxLength={255}
                            placeholder="email@example.com"
                        />
                    </div>
                    <div className="form-group">
                        <label>Phone *</label>
                        <input
                            type="tel"
                            className="form-control"
                            value={formData.phone}
                            onChange={e => setFormData({ ...formData, phone: e.target.value })}
                            required
                            pattern="^[+]?[0-9\s\-\(\)]{8,20}$"
                            placeholder="+79001234567"
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