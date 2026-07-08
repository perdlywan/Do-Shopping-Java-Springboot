'use client';

import { useState } from 'react';
import { doPayment } from '@/app/actions/payment';
import styles from './PayNowButton.module.css';

interface PayNowButtonProps {
  orderId: string;
  amount: number;
}

export default function PayNowButton({ orderId, amount }: PayNowButtonProps) {
  const [isProcessing, setIsProcessing] = useState(false);
  const [showModal, setShowModal] = useState(false);
  const [result, setResult] = useState<{ success?: boolean; error?: string } | null>(null);

  async function handlePay() {
    setIsProcessing(true);
    setResult(null);

    const res = await doPayment(orderId);

    if (res.success) {
      setResult({ success: true });
      // Reload the page after a short delay to show updated status
      setTimeout(() => {
        window.location.reload();
      }, 1500);
    } else {
      setResult({ error: res.error });
    }

    setIsProcessing(false);
  }

  const formattedAmount = new Intl.NumberFormat('id-ID', {
    style: 'currency',
    currency: 'IDR',
    maximumFractionDigits: 0
  }).format(amount);

  return (
    <>
      <button
        className={styles.payBtn}
        onClick={() => setShowModal(true)}
        disabled={isProcessing}
      >
        💳 Pay Now
      </button>

      {showModal && (
        <div className={styles.overlay} onClick={() => !isProcessing && setShowModal(false)}>
          <div className={styles.modal} onClick={e => e.stopPropagation()}>
            <h3 className={styles.modalTitle}>Confirm Payment</h3>
            <p className={styles.modalDesc}>
              You are about to pay <strong>{formattedAmount}</strong> for this order.
            </p>

            {result?.success && (
              <div className={styles.successMsg}>
                ✅ Payment successful! Refreshing...
              </div>
            )}

            {result?.error && (
              <div className={styles.errorMsg}>
                ❌ {result.error}
              </div>
            )}

            {!result?.success && (
              <div className={styles.modalActions}>
                <button
                  className={styles.cancelBtn}
                  onClick={() => setShowModal(false)}
                  disabled={isProcessing}
                >
                  Cancel
                </button>
                <button
                  className={styles.confirmBtn}
                  onClick={handlePay}
                  disabled={isProcessing}
                >
                  {isProcessing ? 'Processing...' : 'Confirm & Pay'}
                </button>
              </div>
            )}
          </div>
        </div>
      )}
    </>
  );
}
