import { useQuery, useMutation } from '@tanstack/react-query';
import { reportApi } from '../api/reportApi';
import { SalesReportRequest } from '../types/report';

export const useReports = () => {
    const useReportStatus = (taskId: string | null) =>
        useQuery({
            queryKey: ['reports', taskId],
            queryFn: () => reportApi.getReportStatus(taskId!),
            enabled: !!taskId,
            refetchInterval: (query) => {
                const data = query.state.data;
                if (data?.status === 'PENDING' || data?.status === 'PROCESSING') {
                    return 2000;
                }
                return false;
            },
        });

    const useGenerateSalesReport = () =>
        useMutation({
            mutationFn: (data: SalesReportRequest) => reportApi.generateSalesReport(data),
        });

    return {
        useReportStatus,
        useGenerateSalesReport,
    };
};