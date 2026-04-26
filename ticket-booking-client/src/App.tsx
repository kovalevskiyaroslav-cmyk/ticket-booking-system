import { BrowserRouter as Router, Routes, Route, Link } from 'react-router-dom';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { EventsPage } from './pages/EventsPage';
import { EventDetailPage } from './pages/EventDetailPage';
import { VenuesPage } from './pages/VenuesPage';
import { VenueDetailPage } from './pages/VenueDetailPage';
import { SeatsPage } from './pages/SeatsPage';
import { SeatDetailPage } from './pages/SeatDetailPage';
import { TicketsPage } from './pages/TicketsPage';
import { OrdersPage } from './pages/OrdersPage';
import { OrderDetailPage } from './pages/OrderDetailPage';
import { UsersPage } from './pages/UsersPage';
import { UserDetailPage } from './pages/UserDetailPage';
import { PaymentsPage } from './pages/PaymentsPage';
import { ReportsPage } from './pages/ReportsPage';
import { Layout } from './components/Layout';
import { TicketDetailPage } from './pages/TicketDetailPage';
import { PaymentDetailPage } from './pages/PaymentDetailPage';
import './App.css';

const queryClient = new QueryClient({
    defaultOptions: {
        queries: {
            staleTime: 30000,
            retry: 1,
        },
    },
});

function App() {
    return (
        <QueryClientProvider client={queryClient}>
            <Router>
                <Routes>
                    <Route path="/" element={<Layout />}>
                        <Route index element={<HomePage />} />
                        <Route path="events" element={<EventsPage />} />
                        <Route path="events/:id" element={<EventDetailPage />} />
                        <Route path="venues" element={<VenuesPage />} />
                        <Route path="venues/:id" element={<VenueDetailPage />} />
                        <Route path="seats" element={<SeatsPage />} />
                        <Route path="tickets" element={<TicketsPage />} />
                        <Route path="orders" element={<OrdersPage />} />
                        <Route path="orders/:id" element={<OrderDetailPage />} />
                        <Route path="users" element={<UsersPage />} />
                        <Route path="users/:id" element={<UserDetailPage />} />
                        <Route path="payments" element={<PaymentsPage />} />
                        <Route path="reports" element={<ReportsPage />} />
                        <Route path="seats/:id" element={<SeatDetailPage />} />
                        <Route path="tickets/:id" element={<TicketDetailPage />} />
                        <Route path="payments/:id" element={<PaymentDetailPage />} />
                    </Route>
                </Routes>
            </Router>
        </QueryClientProvider>
    );
}

function HomePage() {
    return (
        <div>
            <h1>Ticket Booking System</h1>
            <div className="home-grid">
                <Link to="/events" className="home-card">
                    <h2>Events</h2>
                    <p>Manage events</p>
                </Link>
                <Link to="/venues" className="home-card">
                    <h2>Venues</h2>
                    <p>Manage venues</p>
                </Link>
                <Link to="/seats" className="home-card">
                    <h2>Seats</h2>
                    <p>Manage seats</p>
                </Link>
                <Link to="/tickets" className="home-card">
                    <h2>Tickets</h2>
                    <p>View tickets</p>
                </Link>
                <Link to="/orders" className="home-card">
                    <h2>Orders</h2>
                    <p>Manage orders</p>
                </Link>
                <Link to="/users" className="home-card">
                    <h2>Users</h2>
                    <p>Manage users</p>
                </Link>
                <Link to="/payments" className="home-card">
                    <h2>Payments</h2>
                    <p>Manage payments</p>
                </Link>
                <Link to="/reports" className="home-card">
                    <h2>Reports</h2>
                    <p>Generate reports</p>
                </Link>
            </div>
        </div>
    );
}

export default App;