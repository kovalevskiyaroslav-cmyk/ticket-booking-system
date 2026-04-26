import { useState } from 'react';
import { useOrders } from '../hooks/useOrders';
import { useUsers } from '../hooks/useUsers';
import { OrderRequest, OrderUpdate } from '../types/order';
import { OrderStatus } from '../types/common';
import { Modal } from '../components/Modal';
import { LoadingSpinner } from '../components/LoadingSpinner';
import { ErrorMessage } from '../components/ErrorMessage';
import { Link } from 'react-router-dom';
import { UUID } from '../types/common';

const UserName = ({ userId }: { userId: UUID }) => {
    const { useUserById } = useUsers();
    const { data: user, isLoading } = useUserById(userId);

    if (isLoading) return <span>Loading...</span>;
    return (
        <Link to={`/users/${userId}`} style={{ color: '#007bff', textDecoration: 'none' }}>
            {user?.name || userId.substring(0, 8) + '...'}
        </Link>
    );
};

export const OrdersPage = () => {
    const {
        useAllOrders,
        useOrdersByStatus,
        useOrdersByDeleted,
        useOrdersByDateRange,
        useOrdersByVenueName,
        useCreateOrder,
        useCreateOrdersBulk,
        useUpdateOrder,
        useSoftDeleteOrder,
        useHardDeleteOrder
    } = useOrders();

    const { data: orders, isLoading, error, refetch } = useAllOrders();
    const createOrder = useCreateOrder();
    const createOrdersBulk = useCreateOrdersBulk();
    const updateOrder = useUpdateOrder();
    const softDeleteOrder = useSoftDeleteOrder();
    const hardDeleteOrder = useHardDeleteOrder();

    const [isCreateModalOpen, setIsCreateModalOpen] = useState(false);
    const [isBulkModalOpen, setIsBulkModalOpen] = useState(false);
    const [formData, setFormData] = useState({ userId: '' });
    const [bulkFormData, setBulkFormData] = useState('');

    const [searchType, setSearchType] = useState<'all' | 'status' | 'deleted' | 'date' | 'venue'>('all');
    const [statusFilter, setStatusFilter] = useState<OrderStatus>(OrderStatus.CREATED);
    const [deletedFilter, setDeletedFilter] = useState('false');
    const [dateStart, setDateStart] = useState('');
    const [dateEnd, setDateEnd] = useState('');
    const [venueName, setVenueName] = useState('');

    const { data: ordersByStatus, refetch: searchByStatus } = useOrdersByStatus(
        searchType === 'status' ? statusFilter : null
    );
    const { data: ordersByDeleted, refetch: searchByDeleted } = useOrdersByDeleted(
        searchType === 'deleted' ? deletedFilter === 'true' : null
    );
    const { data: ordersByDate, refetch: searchByDate } = useOrdersByDateRange(dateStart, dateEnd);
    const { data: ordersByVenue, refetch: searchByVenue } = useOrdersByVenueName(venueName, { page: 0, size: 20 });

    const displayOrders = (() => {
        if (searchType === 'status' && ordersByStatus) return ordersByStatus;
        if (searchType === 'deleted' && ordersByDeleted) return ordersByDeleted;
        if (searchType === 'date' && ordersByDate) return ordersByDate;
        if (searchType === 'venue' && ordersByVenue) return ordersByVenue.content;
        return orders;
    })();

    const handleSearch = () => {
        if (searchType === 'status') searchByStatus();
        if (searchType === 'deleted') searchByDeleted();
        if (searchType === 'date' && dateStart && dateEnd) searchByDate();
        if (searchType === 'venue' && venueName) searchByVenue();
    };

    const handleCreate = async (e: React.FormEvent) => {
        e.preventDefault();
        try {
            await createOrder.mutateAsync({ userId: formData.userId } as OrderRequest);
            setIsCreateModalOpen(false);
            setFormData({ userId: '' });
        } catch (err: any) {
            console.error('Failed to create order:', err);
            alert(`Error: ${err.response?.data?.detail || err.message}`);
        }
    };

    const handleBulkCreate = async (e: React.FormEvent) => {
        e.preventDefault();
        try {
            const userIds = bulkFormData.split(',').map(id => id.trim()).filter(id => id);
            const ordersData: OrderRequest[] = userIds.map(userId => ({ userId: userId as UUID }));
            await createOrdersBulk.mutateAsync(ordersData);
            setIsBulkModalOpen(false);
            setBulkFormData('');
        } catch (err) {
            console.error('Failed to create bulk orders:', err);
        }
    };

    const handleStatusChange = async (orderId: UUID, newStatus: OrderStatus) => {
        try {
            await updateOrder.mutateAsync({
                id: orderId,
                data: { status: newStatus } as OrderUpdate,
            });
        } catch (err: any) {
            const detail = err.response?.data?.detail || '';
            if (detail.includes('Invalid') || detail.includes('transition') || detail.includes('cannot')) {
                alert(`Cannot change status from current to ${newStatus}`);
            } else if (detail.includes('deleted')) {
                alert('Order is deleted and cannot be modified');
            } else if (detail.includes('not found')) {
                alert('Order not found');
            } else {
                alert(`Error: ${detail}`);
            }
        }
    };

    if (isLoading) return <LoadingSpinner />;
    if (error) return <ErrorMessage message="Error loading orders" onRetry={refetch} />;

    return (
        <div>
            <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '20px' }}>
                <h1>Orders</h1>
                <div style={{ display: 'flex', gap: '10px' }}>
                    <button onClick={() => setIsBulkModalOpen(true)} className="btn btn-success">
                        + Bulk create
                    </button>
                    <button onClick={() => setIsCreateModalOpen(true)} className="btn btn-primary">
                        + Create order
                    </button>
                </div>
            </div>

            <div style={{ display: 'flex', gap: '10px', marginBottom: '20px', alignItems: 'end', flexWrap: 'wrap' }}>
                <div className="form-group" style={{ margin: 0 }}>
                    <label>Search by</label>
                    <select value={searchType} onChange={e => setSearchType(e.target.value as any)} className="form-control">
                        <option value="all">All</option>
                        <option value="status">Status</option>
                        <option value="deleted">Deleted</option>
                        <option value="date">Date</option>
                        <option value="venue">Venue</option>
                    </select>
                </div>

                {searchType === 'status' && (
                    <div className="form-group" style={{ margin: 0 }}>
                        <label>Status</label>
                        <select value={statusFilter} onChange={e => setStatusFilter(e.target.value as OrderStatus)} className="form-control">
                            {Object.values(OrderStatus).map(status => (
                                <option key={status} value={status}>{status}</option>
                            ))}
                        </select>
                    </div>
                )}

                {searchType === 'deleted' && (
                    <div className="form-group" style={{ margin: 0 }}>
                        <label>Deleted</label>
                        <select value={deletedFilter} onChange={e => setDeletedFilter(e.target.value)} className="form-control">
                            <option value="false">Active</option>
                            <option value="true">Deleted</option>
                        </select>
                    </div>
                )}

                {searchType === 'date' && (
                    <>
                        <div className="form-group" style={{ margin: 0 }}>
                            <label>From</label>
                            <input type="datetime-local" className="form-control" value={dateStart} onChange={e => setDateStart(e.target.value)} />
                        </div>
                        <div className="form-group" style={{ margin: 0 }}>
                            <label>To</label>
                            <input type="datetime-local" className="form-control" value={dateEnd} onChange={e => setDateEnd(e.target.value)} />
                        </div>
                    </>
                )}

                {searchType === 'venue' && (
                    <div className="form-group" style={{ margin: 0 }}>
                        <label>Venue</label>
                        <input className="form-control" value={venueName} onChange={e => setVenueName(e.target.value)} placeholder="Venue name" />
                    </div>
                )}

                {searchType !== 'all' && (
                    <button onClick={handleSearch} className="btn btn-primary">Search</button>
                )}
                <button onClick={() => { setSearchType('all'); setVenueName(''); setDateStart(''); setDateEnd(''); }} className="btn">
                    Reset
                </button>
            </div>

            <table>
                <thead>
                <tr>
                    <th>ID</th>
                    <th>User</th>
                    <th>Status</th>
                    <th>Total</th>
                    <th>Tickets</th>
                    <th>Actions</th>
                </tr>
                </thead>
                <tbody>
                {displayOrders?.map(order => (
                    <tr key={order.id}>
                        <td>
                            <Link to={`/orders/${order.id}`} style={{ color: '#007bff', textDecoration: 'none' }}>
                                {order.id.substring(0, 8)}...
                            </Link>
                        </td>
                        <td><UserName userId={order.userId} /></td>
                        <td>
                            <select value={order.status} onChange={e => handleStatusChange(order.id, e.target.value as OrderStatus)} className="form-control" style={{ padding: '4px' }}>
                                {Object.values(OrderStatus).map(status => (
                                    <option key={status} value={status}>{status}</option>
                                ))}
                            </select>
                        </td>
                        <td>
                            <Link to={`/orders/${order.id}`} style={{ color: 'inherit', textDecoration: 'none' }}>
                                {order.totalPrice} ₽
                            </Link>
                        </td>
                        <td>{order.ticketIds.length}</td>
                        <td>
                            <button
                                onClick={async () => {
                                    try {
                                        await softDeleteOrder.mutateAsync(order.id);
                                    } catch (err: any) {
                                        const detail = err.response?.data?.detail || '';
                                        if (detail.includes('already deleted')) {
                                            alert('Order already deleted');
                                        } else if (detail.includes('not found')) {
                                            alert('Order not found');
                                        } else {
                                            alert(`Error: ${detail}`);
                                        }
                                    }
                                }}
                                className="btn"
                                style={{ marginRight: '5px' }}
                            >
                                Soft Delete
                            </button>
                            <button
                                onClick={async () => {
                                    try {
                                        await hardDeleteOrder.mutateAsync(order.id);
                                    } catch (err: any) {
                                        const detail = err.response?.data?.detail || '';
                                        if (detail.includes('not found')) {
                                            alert('Order not found');
                                        } else {
                                            alert(`Error: ${detail}`);
                                        }
                                    }
                                }}
                                className="btn btn-danger"
                            >
                                Delete
                            </button>
                        </td>
                    </tr>
                ))}
                </tbody>
            </table>

            <Modal isOpen={isCreateModalOpen} onClose={() => setIsCreateModalOpen(false)} title="Create order">
                <form onSubmit={handleCreate}>
                    <div className="form-group">
                        <label>User ID *</label>
                        <input className="form-control" value={formData.userId} onChange={e => setFormData({ userId: e.target.value })} required placeholder="User UUID" />
                    </div>
                    <button type="submit" className="btn btn-primary" style={{ width: '100%' }}>
                        Create
                    </button>
                </form>
            </Modal>

            <Modal isOpen={isBulkModalOpen} onClose={() => setIsBulkModalOpen(false)} title="Bulk create orders">
                <form onSubmit={handleBulkCreate}>
                    <div className="form-group">
                        <label>User IDs (comma separated) *</label>
                        <textarea className="form-control" value={bulkFormData} onChange={e => setBulkFormData(e.target.value)} required placeholder="UUID1, UUID2, UUID3" rows={4} />
                    </div>
                    <button type="submit" className="btn btn-success" style={{ width: '100%' }}>
                        Create orders
                    </button>
                </form>
            </Modal>
        </div>
    );
};