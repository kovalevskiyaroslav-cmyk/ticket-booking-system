import { useState } from 'react';
import { usePayments } from '../hooks/usePayments';
import { PaymentStatus, UUID } from '../types/common';
import { PaymentUpdate } from '../types/payment';
import { LoadingSpinner } from '../components/LoadingSpinner';
import { ErrorMessage } from '../components/ErrorMessage';
import { Link } from 'react-router-dom';

export const PaymentsPage = () => {
    const { useAllPayments, usePaymentsByStatus, usePaymentsByAmountRange, useUpdatePayment } = usePayments();
    const { data: payments, isLoading, error, refetch } = useAllPayments();
    const updatePayment = useUpdatePayment();

    const [filterType, setFilterType] = useState<'all' | 'status' | 'amount'>('all');
    const [statusFilter, setStatusFilter] = useState<PaymentStatus>(PaymentStatus.PENDING);
    const [minAmount, setMinAmount] = useState('');
    const [maxAmount, setMaxAmount] = useState('');

    const { data: paymentsByStatus, refetch: searchByStatus } = usePaymentsByStatus(
        filterType === 'status' ? statusFilter : null
    );

    const { data: paymentsByAmount, refetch: searchByAmount } = usePaymentsByAmountRange(
        parseFloat(minAmount) || 0,
        parseFloat(maxAmount) || 0
    );

    const displayPayments = (() => {
        if (filterType === 'status' && paymentsByStatus) return paymentsByStatus;
        if (filterType === 'amount' && paymentsByAmount) return paymentsByAmount;
        return payments;
    })();

    const handleSearch = () => {
        if (filterType === 'status') searchByStatus();
        if (filterType === 'amount' && minAmount && maxAmount) searchByAmount();
    };

    const handleUpdateStatus = async (paymentId: UUID, newStatus: PaymentStatus) => {
        try {
            await updatePayment.mutateAsync({
                id: paymentId,
                data: { status: newStatus } as PaymentUpdate,
            });
        } catch (err: any) {
            const detail = err.response?.data?.detail || '';
            if (detail.includes('Invalid') || detail.includes('transition') || detail.includes('cannot')) {
                alert(`Cannot change payment status from current to ${newStatus}`);
            } else if (detail.includes('not found')) {
                alert('Payment not found');
            } else {
                alert(`Error: ${detail}`);
            }
        }
    };

    if (isLoading) return <LoadingSpinner />;
    if (error) return <ErrorMessage message="Error loading payments" onRetry={refetch} />;

    return (
        <div>
            <h1 style={{ marginBottom: '20px' }}>Payments</h1>

            <div style={{ display: 'flex', gap: '10px', marginBottom: '20px', alignItems: 'end', flexWrap: 'wrap' }}>
                <div className="form-group" style={{ margin: 0 }}>
                    <label>Search by</label>
                    <select
                        value={filterType}
                        onChange={e => setFilterType(e.target.value as any)}
                        className="form-control"
                    >
                        <option value="all">All</option>
                        <option value="status">Status</option>
                        <option value="amount">Amount</option>
                    </select>
                </div>

                {filterType === 'status' && (
                    <div className="form-group" style={{ margin: 0 }}>
                        <label>Status</label>
                        <select
                            value={statusFilter}
                            onChange={e => setStatusFilter(e.target.value as PaymentStatus)}
                            className="form-control"
                        >
                            {Object.values(PaymentStatus).map(status => (
                                <option key={status} value={status}>{status}</option>
                            ))}
                        </select>
                    </div>
                )}

                {filterType === 'amount' && (
                    <>
                        <div className="form-group" style={{ margin: 0 }}>
                            <label>Min amount</label>
                            <input
                                type="number"
                                className="form-control"
                                value={minAmount}
                                onChange={e => setMinAmount(e.target.value)}
                                placeholder="0"
                            />
                        </div>
                        <div className="form-group" style={{ margin: 0 }}>
                            <label>Max amount</label>
                            <input
                                type="number"
                                className="form-control"
                                value={maxAmount}
                                onChange={e => setMaxAmount(e.target.value)}
                                placeholder="100000"
                            />
                        </div>
                    </>
                )}

                {filterType !== 'all' && (
                    <button onClick={handleSearch} className="btn btn-primary">Search</button>
                )}
                <button onClick={() => setFilterType('all')} className="btn">Reset</button>
            </div>

            <table>
                <thead>
                <tr>
                    <th>ID</th>
                    <th>Amount</th>
                    <th>Status</th>
                    <th>Actions</th>
                </tr>
                </thead>
                <tbody>
                {displayPayments?.map(payment => (
                    <tr key={payment.id}>
                        <td>
                            <Link to={`/payments/${payment.id}`} style={{ color: '#007bff', textDecoration: 'none' }}>
                                {payment.id.substring(0, 8)}...
                            </Link>
                        </td>
                        <td>{payment.amount} ₽</td>
                        <td>
                <span style={{
                    padding: '4px 8px',
                    borderRadius: '4px',
                    fontSize: '12px',
                    fontWeight: 600,
                    background:
                        payment.status === PaymentStatus.COMPLETED ? '#d4edda' :
                            payment.status === PaymentStatus.FAILED ? '#f8d7da' :
                                payment.status === PaymentStatus.REFUNDED ? '#fff3cd' :
                                    '#e2e3e5',
                    color:
                        payment.status === PaymentStatus.COMPLETED ? '#155724' :
                            payment.status === PaymentStatus.FAILED ? '#721c24' :
                                payment.status === PaymentStatus.REFUNDED ? '#856404' :
                                    '#383d41'
                }}>
                  {payment.status}
                </span>
                        </td>
                        <td>
                            <select
                                value={payment.status}
                                onChange={e => handleUpdateStatus(payment.id, e.target.value as PaymentStatus)}
                                className="form-control"
                                style={{ padding: '4px', display: 'inline-block', width: 'auto' }}
                            >
                                {Object.values(PaymentStatus).map(status => (
                                    <option key={status} value={status}>{status}</option>
                                ))}
                            </select>
                        </td>
                    </tr>
                ))}
                </tbody>
            </table>
        </div>
    );
};