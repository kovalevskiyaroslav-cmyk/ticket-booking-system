import { useParams, Link } from 'react-router-dom';
import { usePayments } from '../hooks/usePayments';
import { LoadingSpinner } from '../components/LoadingSpinner';
import { ErrorMessage } from '../components/ErrorMessage';
import { UUID, PaymentStatus } from '../types/common';
import { PaymentUpdate } from '../types/payment';
import { useState } from 'react';

const getStatusStyle = (status: PaymentStatus) => {
    const styles = {
        [PaymentStatus.PENDING]: { background: '#e2e3e5', color: '#383d41' },
        [PaymentStatus.COMPLETED]: { background: '#d4edda', color: '#155724' },
        [PaymentStatus.FAILED]: { background: '#f8d7da', color: '#721c24' },
        [PaymentStatus.REFUNDED]: { background: '#fff3cd', color: '#856404' },
    };
    return styles[status] || styles[PaymentStatus.PENDING];
};

export const PaymentDetailPage = () => {
    const { id } = useParams<{ id: string }>();
    const { usePaymentById, useUpdatePayment } = usePayments();
    const updatePayment = useUpdatePayment();

    const { data: payment, isLoading, error, refetch } = usePaymentById(id as UUID);
    const [errorMsg, setErrorMsg] = useState('');

    const handleStatusChange = async (newStatus: PaymentStatus) => {
        setErrorMsg('');
        try {
            await updatePayment.mutateAsync({
                id: id as UUID,
                data: { status: newStatus } as PaymentUpdate,
            });
        } catch (err: any) {
            const detail = err.response?.data?.detail || '';
            if (detail.includes('Invalid') || detail.includes('transition') || detail.includes('cannot')) {
                setErrorMsg(`Cannot change status to ${newStatus}`);
            } else if (detail.includes('not found')) {
                setErrorMsg('Payment not found');
            } else {
                setErrorMsg(`Error: ${detail}`);
            }
        }
    };

    if (isLoading) return <LoadingSpinner />;
    if (error) return <ErrorMessage message="Error loading payment" onRetry={refetch} />;
    if (!payment) return <ErrorMessage message="Payment not found" />;

    const statusStyle = getStatusStyle(payment.status);

    return (
        <div>
            <Link to="/payments" style={{ color: '#007bff', textDecoration: 'none', marginBottom: '20px', display: 'block' }}>
                ← Back to payments
            </Link>

            <div style={{ background: 'white', padding: '30px', borderRadius: '8px', boxShadow: '0 2px 4px rgba(0,0,0,0.1)' }}>
                <h1>Payment {payment.id}</h1>

                {errorMsg && <ErrorMessage message={errorMsg} />}

                <div style={{
                    marginTop: '20px',
                    display: 'grid',
                    gridTemplateColumns: '1fr 1fr',
                    gap: '20px',
                    background: '#f8f9fa',
                    padding: '20px',
                    borderRadius: '8px'
                }}>
                    <div>
                        <p style={{ color: '#666', fontSize: '14px', marginBottom: '8px' }}>Amount</p>
                        <p style={{ fontWeight: '500', fontSize: '24px', color: '#28a745', margin: 0 }}>{payment.amount} ₽</p>
                    </div>
                    <div>
                        <p style={{ color: '#666', fontSize: '14px', marginBottom: '8px' }}>Status</p>
                        <span style={{
                            display: 'inline-block',
                            padding: '8px 16px',
                            borderRadius: '4px',
                            fontSize: '16px',
                            fontWeight: 600,
                            ...statusStyle
                        }}>
                            {payment.status}
                        </span>
                    </div>
                </div>

                <div style={{ marginTop: '20px' }}>
                    <p><strong>Change status:</strong></p>
                    <div style={{ display: 'flex', gap: '10px', marginTop: '10px', flexWrap: 'wrap' }}>
                        {Object.values(PaymentStatus).map(status => (
                            <button
                                key={status}
                                onClick={() => handleStatusChange(status)}
                                className="btn"
                                disabled={status === payment.status || updatePayment.isPending}
                                style={{
                                    background: status === payment.status ? '#6c757d' : getStatusStyle(status).background,
                                    color: getStatusStyle(status).color,
                                    border: status === payment.status ? '2px solid #495057' : '1px solid #ddd',
                                    opacity: status === payment.status ? 0.6 : 1,
                                    cursor: status === payment.status ? 'not-allowed' : 'pointer'
                                }}
                            >
                                {status}
                            </button>
                        ))}
                    </div>
                </div>
            </div>
        </div>
    );
};