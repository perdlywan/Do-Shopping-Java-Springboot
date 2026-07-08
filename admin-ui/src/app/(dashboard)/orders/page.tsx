import { cookies } from 'next/headers';
import Link from 'next/link';
import styles from './page.module.css';

async function getOrders() {
  const cookieStore = await cookies();
  const token = cookieStore.get('token')?.value;

  try {
    const res = await fetch('http://localhost:8080/orders?size=50&sortBy=orderDate&sortDirection=desc', {
      headers: {
        'Authorization': `Bearer ${token}`
      },
      cache: 'no-store'
    });
    
    if (!res.ok) {
      throw new Error('Failed to fetch orders');
    }
    
    return res.json();
  } catch (error) {
    console.error('Error fetching orders:', error);
    return { data: [] };
  }
}

export default async function OrdersPage() {
  const response = await getOrders();
  const orders = response?.data || [];

  return (
    <div className={styles.container}>
      <div className={styles.header}>
        <h1 className={styles.title}>Orders Management</h1>
      </div>

      <div className={styles.tableContainer}>
        <table className={styles.table}>
          <thead>
            <tr>
              <th>Order Number</th>
              <th>Date</th>
              <th>Total Amount</th>
              <th>Status</th>
              <th>Shipping</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            {orders.length > 0 ? (
              orders.map((order: any) => (
                <tr key={order.id}>
                  <td>{order.orderNumber}</td>
                  <td>{new Date(order.orderDate).toLocaleDateString()}</td>
                  <td>Rp {order.totalAmount.toLocaleString('id-ID')}</td>
                  <td>
                    <span className={`${styles.statusBadge} ${styles[order.status] || ''}`}>
                      {order.status}
                    </span>
                  </td>
                  <td>
                    <span className={`${styles.statusBadge} ${styles[order.shipping?.status] || ''}`}>
                      {order.shipping?.status || 'PENDING'}
                    </span>
                  </td>
                  <td className={styles.actions}>
                    <a href={`/orders/${order.id}`} className={styles.editBtn}>
                      View/Edit
                    </a>
                  </td>
                </tr>
              ))
            ) : (
              <tr>
                <td colSpan={6} className={styles.emptyState}>
                  No orders found.
                </td>
              </tr>
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
}
