import { Outlet, Link, useLocation } from 'react-router-dom';

export const Layout = () => {
    const location = useLocation();

    const navItems = [
        { path: '/events', label: 'Events' },
        { path: '/venues', label: 'Venues' },
        { path: '/seats', label: 'Seats' },
        { path: '/tickets', label: 'Tickets' },
        { path: '/orders', label: 'Orders' },
        { path: '/users', label: 'Users' },
        { path: '/payments', label: 'Payments' },
        { path: '/reports', label: 'Reports' },
    ];

    return (
        <div>
            <nav style={{
                background: '#2c3e50',
                padding: '15px 0',
                marginBottom: '30px'
            }}>
                <div className="container" style={{
                    display: 'flex',
                    gap: '20px',
                    flexWrap: 'wrap'
                }}>
                    <Link to="/" style={{
                        color: 'white',
                        textDecoration: 'none',
                        fontWeight: 'bold',
                        fontSize: '18px'
                    }}>
                        🎫 TicketBooking
                    </Link>
                    {navItems.map(item => (
                        <Link
                            key={item.path}
                            to={item.path}
                            style={{
                                color: location.pathname.startsWith(item.path) ? '#3498db' : '#ecf0f1',
                                textDecoration: 'none',
                                padding: '5px 10px',
                                borderRadius: '4px',
                                transition: 'color 0.2s'
                            }}
                        >
                            {item.label}
                        </Link>
                    ))}
                </div>
            </nav>
            <div className="container">
                <Outlet />
            </div>
        </div>
    );
};