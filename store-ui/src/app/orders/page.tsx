import React from 'react';
import { getMyOrders } from '../actions/order';
import PayNowButton from '@/components/PayNowButton';
import styles from './orders.module.css';

export const metadata = {
  title: 'My Orders - Do-Shopping',
};

export default async function OrdersPage({
  searchParams,
}: {
  searchParams?: Promise<{ page?: string }>;
}) {
  const resolvedParams = await searchParams;
  const currentPage = Number(resolvedParams?.page) || 1;
  const res = await getMyOrders(currentPage, 10);

  if (res.error) {
    return (
      <div className={styles.container}>
        <h1 className={styles.title}>My Orders</h1>
        <div className={styles.noOrders}>Error fetching orders: {res.error}</div>
      </div>
    );
  }

  const orders = res.data || [];
  const totalPages = res.meta?.totalPages || 1;

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
        <>
          <div className={styles.ordersList}>
            {orders.map((order: any) => {
              const displayOrderNumber = order.orderNumber || `ORD-${order.id.split('-')[0].toUpperCase()}`;
              const payment = order.payment;
              const isPending = order.status === 'PENDING' && payment?.status === 'PENDING' && payment?.methodType !== 'COD';

              return (
                <div key={order.id} className={styles.orderCard}>
                  <div className={styles.orderHeader}>
                    <div className={styles.orderIdGroup}>
                      <span className={styles.orderLabel}>Order Number</span>
                      <span className={styles.orderIdValue}>{displayOrderNumber}</span>
                    </div>
                    <div className={styles.orderDateGroup}>
                      <span className={styles.orderLabel}>Order Date</span>
                      <span className={styles.orderDateValue}>
                        {new Date(order.orderDate).toLocaleDateString('en-US', {
                          year: 'numeric',
                          month: 'long',
                          day: 'numeric'
                        })}
                      </span>
                    </div>
                    <div className={styles.orderStatusGroup}>
                      <span className={`${styles.statusBadge} ${getStatusClass(order.status)}`}>
                        {order.status}
                      </span>
                    </div>
                  </div>

                  <div className={styles.orderItems}>
                    {order.items?.map((item: any) => (
                      <div key={item.id} className={styles.orderItem}>
                        <div className={styles.itemInfo}>
                          <span className={styles.itemName}>{item.productName}</span>
                          <span className={styles.itemMeta}>Qty: {item.quantity}</span>
                        </div>
                        <span className={styles.itemPrice}>
                          Rp {item.subTotal.toLocaleString('id-ID')}
                        </span>
                      </div>
                    ))}
                  </div>

                  <div className={styles.orderFooter}>
                    <div className={styles.footerRow}>
                      <span className={styles.footerLabel}>Total Quantity</span>
                      <span className={styles.footerValue}>{order.totalQuantity} items</span>
                    </div>
                    <div className={styles.footerRow}>
                      <span className={styles.footerLabel}>Total Amount</span>
                      <span className={styles.footerTotal}>Rp {order.totalAmount.toLocaleString('id-ID')}</span>
                    </div>
                  </div>
                  
                  {payment && (
                    <div className={styles.paymentSection}>
                      <h4 className={styles.paymentTitle}>Payment Details</h4>
                      <div className={styles.paymentRow}>
                        <span className={styles.paymentLabel}>Method</span>
                        <span className={styles.paymentValue}>
                          {payment.methodType} {payment.providerName ? `(${payment.providerName})` : ''}
                        </span>
                      </div>
                      <div className={styles.paymentRow}>
                        <span className={styles.paymentLabel}>Status</span>
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

          {totalPages > 1 && (
            <div className={styles.pagination}>
              {currentPage > 1 ? (
                <a href={`/orders?page=${currentPage - 1}`} className={styles.pageBtn}>
                  Previous
                </a>
              ) : (
                <button className={styles.pageBtn} disabled>Previous</button>
              )}
              
              <span className={styles.pageInfo}>
                Page {currentPage} of {totalPages}
              </span>

              {currentPage < totalPages ? (
                <a href={`/orders?page=${currentPage + 1}`} className={styles.pageBtn}>
                  Next
                </a>
              ) : (
                <button className={styles.pageBtn} disabled>Next</button>
              )}
            </div>
          )}
        </>
      )}
    </div>
  );
}
