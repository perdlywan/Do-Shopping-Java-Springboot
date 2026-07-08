'use client';

import { useActionState } from 'react';
import { updateOrderAction } from '@/app/actions/order';
import styles from '@/app/(dashboard)/orders/[id]/page.module.css';

interface OrderUpdateFormProps {
  orderId: string;
  initialData: {
    orderStatus: string;
    shippingStatus?: string;
    courierName?: string;
    serviceType?: string;
    trackingNumber?: string;
    shippingCost?: number;
    shippedAt?: string;
    deliveredAt?: string;
  };
}

const ORDER_STATUSES = ['PENDING', 'PAID', 'PROCESSING', 'SHIPPED', 'COMPLETED', 'CANCELLED'];
const SHIPPING_STATUSES = ['PENDING', 'PACKED', 'SHIPPED', 'DELIVERED', 'CANCELLED'];

// Helper to format ISO string (e.g. "2024-05-10T12:00:00") to "YYYY-MM-DDTHH:mm" for datetime-local input
const formatForInput = (isoString?: string) => {
  if (!isoString) return '';
  return isoString.substring(0, 16);
};

export default function OrderUpdateForm({ orderId, initialData }: OrderUpdateFormProps) {
  const updateWithId = updateOrderAction.bind(null, orderId);
  const [state, formAction, isPending] = useActionState(updateWithId, null);

  // Filter available statuses based on current status
  const currentOrderStatus = initialData.orderStatus;
  const currentShippingStatus = initialData.shippingStatus || 'PENDING';

  const isOrderTerminal = currentOrderStatus === 'COMPLETED' || currentOrderStatus === 'CANCELLED';
  const isShippingTerminal = currentShippingStatus === 'DELIVERED' || currentShippingStatus === 'CANCELLED';

  const availableOrderStatuses = isOrderTerminal 
    ? [currentOrderStatus] 
    : ORDER_STATUSES;

  const availableShippingStatuses = isShippingTerminal 
    ? [currentShippingStatus] 
    : SHIPPING_STATUSES;

  return (
    <div className={styles.card}>
      <form action={formAction}>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1.5rem', paddingBottom: '1rem', borderBottom: '1px solid var(--border-light)' }}>
          <h2 style={{ fontSize: '1.25rem', fontWeight: 600, margin: 0 }}>Update Order</h2>
          <button 
            type="submit" 
            style={{ 
              backgroundColor: '#10b981', 
              color: 'white', 
              border: 'none', 
              borderRadius: '0.5rem', 
              padding: '0.75rem 1.5rem', 
              fontWeight: 600, 
              cursor: 'pointer', 
              boxShadow: '0 4px 6px -1px rgba(16, 185, 129, 0.4), 0 2px 4px -1px rgba(16, 185, 129, 0.2)',
              display: 'flex',
              alignItems: 'center',
              gap: '0.5rem',
              transition: 'all 0.2s'
            }} 
            disabled={isPending}
          >
            {isPending ? 'Updating...' : 'SAVE CHANGES'}
          </button>
        </div>
        {state?.error && <div className={styles.errorMsg}>{state.error}</div>}

        <div className={styles.formGroup}>
          <label className={styles.label}>Order Status</label>
          <select name="orderStatus" className={styles.select} defaultValue={currentOrderStatus} disabled={isOrderTerminal}>
            {availableOrderStatuses.map(status => (
              <option key={status} value={status}>{status}</option>
            ))}
          </select>
        </div>

        <div className={styles.formGroup}>
          <label className={styles.label}>Shipping Status</label>
          <select name="shippingStatus" className={styles.select} defaultValue={currentShippingStatus} disabled={isShippingTerminal}>
            {availableShippingStatuses.map(status => (
              <option key={status} value={status}>{status}</option>
            ))}
          </select>
        </div>

        <div className={styles.formGroup}>
          <label className={styles.label}>Courier Name</label>
          <input 
            type="text" 
            name="courierName" 
            className={styles.input} 
            defaultValue={initialData.courierName || ''} 
            placeholder="e.g. JNE, SiCepat"
          />
        </div>

        <div className={styles.formGroup}>
          <label className={styles.label}>Service Type</label>
          <input 
            type="text" 
            name="serviceType" 
            className={styles.input} 
            defaultValue={initialData.serviceType || ''} 
            placeholder="e.g. REG, YES"
          />
        </div>

        <div className={styles.formGroup}>
          <label className={styles.label}>Tracking Number</label>
          <input 
            type="text" 
            name="trackingNumber" 
            className={styles.input} 
            defaultValue={initialData.trackingNumber || ''} 
            placeholder="Resi"
          />
        </div>

        <div className={styles.formGroup}>
          <label className={styles.label}>Shipping Cost (Rp)</label>
          <input 
            type="number" 
            name="shippingCost" 
            className={styles.input} 
            defaultValue={initialData.shippingCost || 0} 
            min="0"
          />
        </div>

        <div className={styles.formGroup}>
          <label className={styles.label}>Shipped At</label>
          <input 
            type="datetime-local" 
            name="shippedAt" 
            className={styles.input} 
            defaultValue={formatForInput(initialData.shippedAt)} 
          />
        </div>

        <div className={styles.formGroup}>
          <label className={styles.label}>Delivered At</label>
          <input 
            type="datetime-local" 
            name="deliveredAt" 
            className={styles.input} 
            defaultValue={formatForInput(initialData.deliveredAt)} 
          />
        </div>

      </form>
    </div>
  );
}
