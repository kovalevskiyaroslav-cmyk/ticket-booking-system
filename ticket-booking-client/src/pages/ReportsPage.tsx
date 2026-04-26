import { useState } from 'react';
import { useReports } from '../hooks/useReports';
import { LoadingSpinner } from '../components/LoadingSpinner';
import { ErrorMessage } from '../components/ErrorMessage';

export const ReportsPage = () => {
    const { useGenerateSalesReport, useReportStatus } = useReports();
    const generateReport = useGenerateSalesReport();

    const [formData, setFormData] = useState({
        fromDate: '',
        toDate: '',
        groupBy: 'event',
    });

    const [taskId, setTaskId] = useState<string | null>(null);
    const { data: reportTask, isLoading, error } = useReportStatus(taskId);

    const handleGenerate = async (e: React.FormEvent) => {
        e.preventDefault();
        try {
            const result = await generateReport.mutateAsync({
                fromDate: formData.fromDate ? new Date(formData.fromDate).toISOString() : undefined,
                toDate: formData.toDate ? new Date(formData.toDate).toISOString() : undefined,
                groupBy: formData.groupBy,
            });
            setTaskId(result.taskId);
        } catch (err) {
            console.error('Failed to generate report:', err);
        }
    };

    return (
        <div>
            <h1 style={{ marginBottom: '20px' }}>Sales reports</h1>

            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '30px' }}>
                <div style={{ background: 'white', padding: '30px', borderRadius: '8px', boxShadow: '0 2px 4px rgba(0,0,0,0.1)' }}>
                    <h2>Create report</h2>
                    <form onSubmit={handleGenerate} style={{ marginTop: '20px' }}>
                        <div className="form-group">
                            <label>From date</label>
                            <input
                                type="datetime-local"
                                className="form-control"
                                value={formData.fromDate}
                                onChange={e => setFormData({ ...formData, fromDate: e.target.value })}
                            />
                        </div>
                        <div className="form-group">
                            <label>To date</label>
                            <input
                                type="datetime-local"
                                className="form-control"
                                value={formData.toDate}
                                onChange={e => setFormData({ ...formData, toDate: e.target.value })}
                            />
                        </div>
                        <div className="form-group">
                            <label>Group by</label>
                            <select
                                className="form-control"
                                value={formData.groupBy}
                                onChange={e => setFormData({ ...formData, groupBy: e.target.value })}
                            >
                                <option value="event">By event</option>
                                <option value="venue">By venue</option>
                                <option value="date">By date</option>
                            </select>
                        </div>
                        <button
                            type="submit"
                            className="btn btn-primary"
                            style={{ width: '100%' }}
                            disabled={generateReport.isPending}
                        >
                            {generateReport.isPending ? 'Generating...' : 'Generate report'}
                        </button>
                    </form>
                </div>

                <div style={{ background: 'white', padding: '30px', borderRadius: '8px', boxShadow: '0 2px 4px rgba(0,0,0,0.1)' }}>
                    <h2>Results</h2>

                    {!taskId && (
                        <p style={{ marginTop: '20px', color: '#666' }}>Create a report to see results</p>
                    )}

                    {taskId && isLoading && <LoadingSpinner />}

                    {error && (
                        <ErrorMessage message="Error loading report" />
                    )}

                    {reportTask && (
                        <div style={{ marginTop: '20px' }}>
                            <div style={{ marginBottom: '15px' }}>
                                <p><strong>Status:</strong> {reportTask.status}</p>
                                <p><strong>Task ID:</strong> {reportTask.taskId}</p>
                                {reportTask.message && <p><strong>Message:</strong> {reportTask.message}</p>}
                                {reportTask.createdAt && (
                                    <p><strong>Created:</strong> {new Date(reportTask.createdAt).toLocaleString('en-US')}</p>
                                )}
                            </div>

                            {reportTask.result && (
                                <div style={{ borderTop: '1px solid #eee', paddingTop: '15px' }}>
                                    <h3 style={{ marginBottom: '10px' }}>Summary</h3>
                                    <div style={{
                                        display: 'grid',
                                        gridTemplateColumns: '1fr 1fr',
                                        gap: '10px',
                                        background: '#f8f9fa',
                                        padding: '15px',
                                        borderRadius: '4px',
                                        marginBottom: '15px'
                                    }}>
                                        <div>
                                            <p style={{ color: '#666', fontSize: '14px' }}>Tickets sold</p>
                                            <p style={{ fontSize: '24px', fontWeight: 'bold' }}>{reportTask.result.totalTicketsSold}</p>
                                        </div>
                                        <div>
                                            <p style={{ color: '#666', fontSize: '14px' }}>Revenue</p>
                                            <p style={{ fontSize: '24px', fontWeight: 'bold', color: '#28a745' }}>
                                                {reportTask.result.totalRevenue} ₽
                                            </p>
                                        </div>
                                    </div>
                                </div>
                            )}
                        </div>
                    )}
                </div>
            </div>
        </div>
    );
};