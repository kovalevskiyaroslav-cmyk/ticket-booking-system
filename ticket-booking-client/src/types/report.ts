export interface SalesReportRequest {
    fromDate?: string;
    toDate?: string;
    groupBy?: string;
}

export interface EventSales {
    eventName: string;
    ticketsSold: number;
    revenue: number;
}

export interface SalesReportResponse {
    reportId: string;
    generatedAt: string;
    totalTicketsSold: number;
    totalRevenue: number;
    salesByEvent: Record<string, EventSales>;
}

export interface ReportTask {
    taskId: string;
    status: string;
    message: string;
    result: SalesReportResponse | null;
    createdAt: string;
}