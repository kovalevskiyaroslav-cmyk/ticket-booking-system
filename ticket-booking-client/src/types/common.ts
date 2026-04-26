// Базовые типы, используемые во всем приложении
export type UUID = string;

export interface Pageable {
    page: number;
    size: number;
    sort?: string[];
}

export interface Page<T> {
    content: T[];
    totalElements: number;
    totalPages: number;
    number: number;
    size: number;
    first: boolean;
    last: boolean;
    empty: boolean;
}

export enum OrderStatus {
    CREATED = 'CREATED',
    PAID = 'PAID',
    CANCELLED = 'CANCELLED',
    REFUNDED = 'REFUNDED',
}

export enum PaymentStatus {
    PENDING = 'PENDING',
    COMPLETED = 'COMPLETED',
    FAILED = 'FAILED',
    REFUNDED = 'REFUNDED',
}