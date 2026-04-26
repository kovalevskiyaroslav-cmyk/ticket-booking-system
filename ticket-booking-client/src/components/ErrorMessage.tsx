interface ErrorMessageProps {
    message: string;
    onRetry?: () => void;
}

export const ErrorMessage = ({ message, onRetry }: ErrorMessageProps) => (
    <div className="error-message">
        <p>{message}</p>
        {onRetry && (
            <button onClick={onRetry} className="btn btn-primary" style={{ marginTop: '10px' }}>
                Retry
            </button>
        )}
    </div>
);