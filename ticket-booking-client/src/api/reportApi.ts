import apiClient from './client';
import { SalesReportRequest, ReportTask } from '../types/report';

export const reportApi = {
    generateSalesReport: (data: SalesReportRequest) =>
        apiClient.post<{ taskId: string; status: string; message: string }>('/reports/sales', data)
            .then(res => res.data),

    getReportStatus: (taskId: string) =>
        apiClient.get<ReportTask>(`/reports/${taskId}/status`).then(res => res.data),
};