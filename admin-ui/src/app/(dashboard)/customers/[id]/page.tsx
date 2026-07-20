import Link from 'next/link';
import { cookies } from 'next/headers';
import { notFound } from 'next/navigation';
import CustomerStatusToggle from './CustomerStatusToggle';
import styles from '../../products/page.module.css';

interface CustomerDetail {
  id: string;
  username: string;
  name: string;
  email: string;
  phone: string;
  totalOrders: number;
  totalSpent: number;
  status: string;
  createdAt: string;
  recentOrders: {
    data: any[];
    totalPages: number;
  };
  addresses?: Array<{
    id: string;
    address: string;
    city: string;
    state: string;
    country: string;
    postalCode: string;
    is_default: boolean;
  }>;
}

async function getCustomerDetail(id: string) {
  try {
    const cookieStore = await cookies();
    const token = cookieStore.get('token')?.value;

    const headers: HeadersInit = {};
    if (token) {
      headers['Authorization'] = `Bearer ${token}`;
    }

    const res = await fetch(`http://localhost:8080/customers/${id}`, {
      cache: 'no-store',
      headers
    });

    if (!res.ok) {
      if (res.status === 404) return null;
      throw new Error(`Failed to fetch customer: ${res.status}`);
    }

    const json = await res.json();
    return json.data as CustomerDetail;
  } catch (error) {
    console.error(error);
    return null;
  }
}

export default async function CustomerDetailPage({
  params,
}: {
  params: Promise<{ id: string }>;
}) {
  const resolvedParams = await params;
  const customer = await getCustomerDetail(resolvedParams.id);

  if (!customer) {
    notFound();
  }

  const formatCurrency = (amount: number) => {
    return new Intl.NumberFormat('id-ID', {
      style: 'currency',
      currency: 'IDR',
      minimumFractionDigits: 0
    }).format(amount);
  };

  return (
    <div className={styles.container}>
      <div className={styles.header}>
        <h1 className={styles.title}>Customer Profile</h1>
        <div style={{ display: 'flex', gap: '1rem' }}>
          <CustomerStatusToggle id={customer.id} status={customer.status} />
        </div>
      </div>

      <div className={styles.card} style={{ marginBottom: '2rem' }}>
        <h2 style={{ marginBottom: '1rem' }}>Basic Information</h2>
        <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '1rem' }}>
          <div>
            <p><strong>Name:</strong> {customer.name}</p>
            <p><strong>Username:</strong> {customer.username}</p>
            <p><strong>Email:</strong> {customer.email}</p>
          </div>
          <div>
            <p><strong>Phone:</strong> {customer.phone || '-'}</p>
            <p><strong>Status:</strong> <span style={{ color: customer.status === 'Active' ? '#137333' : '#c5221f', fontWeight: 'bold' }}>{customer.status}</span></p>
            <p><strong>Joined:</strong> {new Date(customer.createdAt).toLocaleDateString()}</p>
          </div>
        </div>
      </div>

      <div className={styles.card} style={{ marginBottom: '2rem' }}>
        <h2 style={{ marginBottom: '1rem' }}>Order Summary</h2>
        <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '1rem' }}>
          <div>
            <p><strong>Total Orders:</strong> {customer.totalOrders}</p>
          </div>
          <div>
            <p><strong>Total Spent:</strong> {formatCurrency(customer.totalSpent)}</p>
          </div>
        </div>
      </div>

      <div className={styles.card} style={{ marginBottom: '2rem' }}>
        <h2 style={{ marginBottom: '1rem' }}>Addresses</h2>
        <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
          {customer.addresses && customer.addresses.length > 0 ? (
            customer.addresses.map((addr) => (
              <div key={addr.id} style={{ border: '1px solid #eee', padding: '1rem', borderRadius: '4px' }}>
                <p>
                  <strong>{addr.address}</strong> {addr.is_default && <span style={{ fontSize: '0.8rem', backgroundColor: '#e6f4ea', color: '#137333', padding: '2px 6px', borderRadius: '4px', marginLeft: '8px' }}>Default</span>}
                </p>
                <p style={{ color: '#555', marginTop: '4px' }}>{addr.city}, {addr.state} {addr.postalCode}</p>
                <p style={{ color: '#777' }}>{addr.country}</p>
              </div>
            ))
          ) : (
            <p>No addresses found.</p>
          )}
        </div>
      </div>

      <div className={styles.card}>
        <h2 style={{ marginBottom: '1rem' }}>Recent Orders</h2>
        <table className={styles.table}>
          <thead>
            <tr>
              <th>Order Number</th>
              <th>Date</th>
              <th>Status</th>
              <th>Total Amount</th>
              <th>Action</th>
            </tr>
          </thead>
          <tbody>
            {customer.recentOrders?.data?.length === 0 ? (
              <tr>
                <td colSpan={5} style={{ textAlign: 'center', padding: '2rem' }}>
                  No recent orders found.
                </td>
              </tr>
            ) : (
              customer.recentOrders?.data?.map((order) => (
                <tr key={order.id}>
                  <td>{order.orderNumber}</td>
                  <td>{new Date(order.orderDate).toLocaleString()}</td>
                  <td>{order.status}</td>
                  <td className={styles.price}>{formatCurrency(order.totalAmount)}</td>
                  <td>
                    <Link href={`/orders/${order.id}`} style={{ color: '#0066cc', textDecoration: 'none' }}>
                      View Order
                    </Link>
                  </td>
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
}
