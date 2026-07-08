import React from 'react';
import { getMyOrders } from '../actions/order';
import PayNowButton from '@/components/PayNowButton';
import styles from './orders.module.css';

export const metadata = {
  title: 'My Orders - Do-Shopping',
};

export default async function OrdersPage() {
  const res = await getMyOrders(1, 20);

  if (res.error) {
    return (
      <div className={styles.container}>
        <h1 className={styles.title}>My Orders</h1>
        <div className={styles.noOrders}>Error fetching orders: {res.error}</div>
      </div>
    );
  }

  const orders = res.data || [];

  function getStatusClass(status: string) {
    switch (status) {
      case 'PENDING': return styles.statusPending;
      case 'PAID': return styles.statusPaid;
      case 'PROCESSING': return styles.statusProcessing;
      case 'SHIPPED': return styles.statusShipped;
      case 'COMPLETED': return styles.statusCompleted;
      case 'CANCELLED': return styles.statusCancelled;
      default: return '';
    }
  }

  function getPaymentStatusClass(status: string) {
    switch (status) {
      case 'PENDING': return styles.paymentPending;
      case 'PAID': return styles.paymentPaid;
      case 'FAILED': return styles.paymentFailed;
      case 'EXPIRED': return styles.paymentExpired;
      case 'CANCELLED': return styles.paymentCancelled;
      default: return '';
    }
  }

  return (
    <div className={styles.container}>
      <h1 className={styles.title}>My Orders</h1>

      {orders.length === 0 ? (
        <div className={styles.noOrders}>You haven&apos;t placed any orders yet.</div>
      ) : (
        <div className={styles.ordersList}>
          {orders.map((order: any) => {
            const displayOrderNumber = order.orderNumber || `ORD-${order.id.split('-')[0].toUpperCase()}`;
            const payment = order.payment;
            const isPending = order.status === 'PENDING' && payment?.status === 'PENDING' && payment?.methodType !== 'COD';

            return (
              <div key={order.id} className={styles.orderCard}>
                <div className={styles.orderHeader}>
                  <div>
                    <div className={styles.orderId}>Order #{displayOrderNumber}</div>
                    <div className={styles.orderDate}>
                      {new Date(order.orderDate).toLocaleDateString('en-US', {
                        year: 'numeric',
                        month: 'long',
                        day: 'numeric',
                        hour: '2-digit',
                        minute: '2-digit'
                      })}
                    </div>
                  </div>
                  <div className={`${styles.orderStatus} ${getStatusClass(order.status)}`}>
                    {order.status}
                  </div>
                </div>

                <div className={styles.orderBody}>
                  <div className={styles.orderDetails}>
                    <ul className={styles.itemList}>
                      {(order.orderDetail || []).map((item: any) => (
                        <li key={item.id} className={styles.item}>
                          <span className={styles.itemName}>{item.productName}</span>
                          <span className={styles.itemQty}>x {item.quantity}</span>
                        </li>
                      ))}
                    </ul>
                  </div>

                  <div className={styles.orderSummary}>
                    <div className={styles.totalLabel}>Total Amount</div>
                    <div className={styles.totalAmount}>
                      {new Intl.NumberFormat('id-ID', {
                        style: 'currency', currency: 'IDR', maximumFractionDigits: 0
                      }).format(order.totalAmount)}
                    </div>
                  </div>
                </div>

                {/* Payment Section */}
                {payment && (
                  <div className={styles.paymentSection}>
                    <div className={styles.paymentInfo}>
                      <div className={styles.paymentRow}>
                        <span className={styles.paymentLabel}>Payment Method</span>
                        <span className={styles.paymentValue}>
                          {payment.methodType?.replace('_', ' ')}
                          {payment.providerName && ` — ${payment.providerName}`}
                        </span>
                      </div>
                      <div className={styles.paymentRow}>
                        <span className={styles.paymentLabel}>Payment Status</span>
                        <span className={`${styles.paymentBadge} ${getPaymentStatusClass(payment.status)}`}>
                          {payment.status}
                        </span>
                      </div>
                      {payment.paidAt && (
                        <div className={styles.paymentRow}>
                          <span className={styles.paymentLabel}>Paid At</span>
                          <span className={styles.paymentValue}>
                            {new Date(payment.paidAt).toLocaleDateString('en-US', {
                              year: 'numeric', month: 'long', day: 'numeric',
                              hour: '2-digit', minute: '2-digit'
                            })}
                          </span>
                        </div>
                      )}
                      {payment.paymentExpiredAt && payment.status === 'PENDING' && (
                        <div className={styles.paymentRow}>
                          <span className={styles.paymentLabel}>Expires At</span>
                          <span className={styles.paymentValue} style={{ color: '#dc2626' }}>
                            {new Date(payment.paymentExpiredAt).toLocaleDateString('en-US', {
                              year: 'numeric', month: 'long', day: 'numeric',
                              hour: '2-digit', minute: '2-digit'
                            })}
                          </span>
                        </div>
                      )}
                    </div>

                    {isPending && (
                      <div className={styles.payAction}>
                        <PayNowButton orderId={order.id} amount={order.totalAmount} />
                      </div>
                    )}
                  </div>
                )}
              </div>
            );
          })}
        </div>
      )}
    </div>
  );
}
