import { cookies } from 'next/headers';
import Link from 'next/link';
import { notFound } from 'next/navigation';
import styles from './page.module.css';
import OrderUpdateForm from '@/components/orders/OrderUpdateForm';

async function getOrder(id: string) {
  const cookieStore = await cookies();
  const token = cookieStore.get('token')?.value;

  try {
    const res = await fetch(`http://localhost:8080/orders/${id}`, {
      headers: {
        'Authorization': `Bearer ${token}`
      },
      cache: 'no-store'
    });
    
    if (!res.ok) {
      const errorText = await res.text();
      if (res.status === 404) {
         throw new Error(`API 404 Not Found. Body: ${errorText}. Requested ID: ${id}`);
      }
      throw new Error(`Failed to fetch order (Status: ${res.status}). Body: ${errorText}`);
    }
    
    return await res.json();
  } catch (error: any) {
    console.error('Error fetching order:', error);
    throw error;
  }
}

export default async function OrderDetailPage({ params }: { params: Promise<{ id: string }> }) {
  let id: string;
  try {
    // Next 15 uses promise, Next 14 uses object. This safely handles both.
    const resolvedParams = await Promise.resolve(params);
    id = resolvedParams.id;
  } catch (e) {
    return <div className={styles.container}>Error resolving params.</div>;
  }

  if (!id) {
    return <div className={styles.container}>Invalid Order ID.</div>;
  }

  let response;
  try {
    response = await getOrder(id);
  } catch (error: any) {
    return <div className={styles.container}><h2>Error Loading Order</h2><p>{error.message}</p></div>;
  }

  const order = response?.data;

  if (!order) {
    notFound();
  }

  const initialData = {
    orderStatus: order.status,
    shippingStatus: order.shipping?.status,
    courierName: order.shipping?.courierName,
    serviceType: order.shipping?.serviceType,
    trackingNumber: order.shipping?.trackingNumber,
    shippingCost: order.shipping?.shippingCost,
    shippedAt: order.shipping?.shippedAt,
    deliveredAt: order.shipping?.deliveredAt,
  };

  return (
    <div className={styles.container}>
      <div className={styles.header}>
        <Link href="/orders" className={styles.backBtn}>&larr; Back to Orders</Link>
        <h1 className={styles.title}>Order #{order.orderNumber}</h1>
      </div>

      <div className={styles.content}>
        <div className={styles.detailsCol}>
          <div className={styles.card}>
            <h2 className={styles.cardTitle}>Order Summary</h2>
            <div className={styles.detailRow}>
              <span className={styles.detailLabel}>Date</span>
              <span className={styles.detailValue}>{new Date(order.orderDate).toLocaleString('id-ID')}</span>
            </div>
            <div className={styles.detailRow}>
              <span className={styles.detailLabel}>Total Quantity</span>
              <span className={styles.detailValue}>{order.totalQuantity} items</span>
            </div>
            <div className={styles.detailRow}>
              <span className={styles.detailLabel}>Total Amount</span>
              <span className={styles.detailValue}>Rp {order.totalAmount.toLocaleString('id-ID')}</span>
            </div>
            {order.note && (
              <div className={styles.detailRow}>
                <span className={styles.detailLabel}>Note</span>
                <span className={styles.detailValue}>{order.note}</span>
              </div>
            )}
            
            <h3 className={styles.cardTitle} style={{ marginTop: '2rem' }}>Payment Info</h3>
            <div className={styles.detailRow}>
              <span className={styles.detailLabel}>Method</span>
              <span className={styles.detailValue}>{order.payment?.methodType || 'N/A'}</span>
            </div>
            <div className={styles.detailRow}>
              <span className={styles.detailLabel}>Provider</span>
              <span className={styles.detailValue}>{order.payment?.providerName || 'N/A'}</span>
            </div>
            <div className={styles.detailRow}>
              <span className={styles.detailLabel}>Status</span>
              <span className={styles.detailValue}>{order.payment?.status || 'N/A'}</span>
            </div>

            <h3 className={styles.cardTitle} style={{ marginTop: '2rem' }}>Items</h3>
            <div className={styles.detailsList}>
              {order.orderDetail?.map((item: any) => (
                <div key={item.id} className={styles.itemRow}>
                  <div>
                    <div className={styles.itemName}>{item.productName}</div>
                    <div className={styles.itemSub}>{item.quantity} x Rp {item.price.toLocaleString('id-ID')}</div>
                  </div>
                  <div className={styles.itemTotal}>
                    Rp {item.subTotal.toLocaleString('id-ID')}
                  </div>
                </div>
              ))}
            </div>
          </div>
        </div>

        <div className={styles.formCol}>
          <OrderUpdateForm orderId={order.id} initialData={initialData} />
        </div>
      </div>
    </div>
  );
}
