'use client';

import React, { useState, useTransition } from 'react';
import styles from './cart.module.css';
import { useCart } from '@/context/CartContext';
import { placeOrder } from '@/app/actions/order';
import { useRouter } from 'next/navigation';
import Link from 'next/link';

type Address = {
  id: string;
  address: string;
  city: string;
  state: string;
  country: string;
  postalCode: string;
  isDefault: boolean;
};

interface CheckoutFormProps {
  addresses: Address[];
}

export default function CheckoutForm({ addresses }: CheckoutFormProps) {
  const { cartItems, cartTotalQuantity, cartTotalPrice, clearCart } = useCart();
  const [selectedAddress, setSelectedAddress] = useState<string>(
    addresses.find(a => a.isDefault)?.id || (addresses.length > 0 ? addresses[0].id : '')
  );
  const [paymentMethod, setPaymentMethod] = useState<string>('COD');
  const [note, setNote] = useState<string>('');
  const [providerName, setProviderName] = useState<string>('');
  const [isPending, startTransition] = useTransition();
  const [error, setError] = useState<string | null>(null);
  const [showSuccessModal, setShowSuccessModal] = useState(false);
  const router = useRouter();

  if (cartItems.length === 0) {
    return null;
  }

  const handleCheckout = () => {
    if (!selectedAddress) {
      setError('Please select a shipping address. If you don\'t have one, please add it in your profile.');
      return;
    }
    
    if (paymentMethod !== 'COD' && !providerName.trim()) {
      setError('Provider Name is required for non-COD payment methods.');
      return;
    }

    setError(null);

    const payload = {
      items: cartItems.map(item => ({
        productId: item.productId,
        quantity: item.quantity
      })),
      note: note,
      paymentMethod: paymentMethod,
      providerName: paymentMethod === 'COD' ? 'Internal' : providerName,
      shippingAddressId: selectedAddress
    };

    startTransition(async () => {
      const res = await placeOrder(payload);
      if (res.error) {
        setError(res.error);
      } else if (res.success) {
        clearCart();
        setShowSuccessModal(true);
      }
    });
  };

  return (
    <div className={styles.summaryPanel}>
      <h2 className={styles.summaryTitle}>Order Summary</h2>
      
      <div className={styles.summaryRow}>
        <span>Items ({cartTotalQuantity}):</span>
        <span>
          {new Intl.NumberFormat('id-ID', { 
            style: 'currency', currency: 'IDR', maximumFractionDigits: 0
          }).format(cartTotalPrice)}
        </span>
      </div>

      <div className={styles.summaryRow}>
        <span>Shipping:</span>
        <span>Free</span>
      </div>

      <div className={`${styles.summaryRow} ${styles.total}`}>
        <span>Total:</span>
        <span>
          {new Intl.NumberFormat('id-ID', { 
            style: 'currency', currency: 'IDR', maximumFractionDigits: 0
          }).format(cartTotalPrice)}
        </span>
      </div>

      <div className={styles.checkoutForm}>
        <div className={styles.formGroup}>
          <label className={styles.label}>Shipping Address</label>
          {addresses.length === 0 ? (
            <p style={{ fontSize: '0.875rem', color: '#ef4444', marginBottom: '0.5rem' }}>
              You have no shipping addresses. <Link href="/shipping-addresses" style={{ textDecoration: 'underline' }}>Add one here</Link>.
            </p>
          ) : (
            <select 
              className={styles.select}
              value={selectedAddress}
              onChange={(e) => setSelectedAddress(e.target.value)}
            >
              {addresses.map(addr => (
                <option key={addr.id} value={addr.id}>
                  {addr.address}, {addr.city} {addr.isDefault ? '(Default)' : ''}
                </option>
              ))}
            </select>
          )}
        </div>

        <div className={styles.formGroup}>
          <label className={styles.label}>Payment Method</label>
          <select 
            className={styles.select}
            value={paymentMethod}
            onChange={(e) => setPaymentMethod(e.target.value)}
          >
            <option value="TRANSFER_BANK">Bank Transfer</option>
            <option value="E_WALLET">E-Wallet</option>
            <option value="VIRTUAL_ACCOUNT">Virtual Account</option>
            <option value="COD">Cash on Delivery (COD)</option>
          </select>
        </div>

        {paymentMethod !== 'COD' && (
          <div className={styles.formGroup}>
            <label className={styles.label}>Provider Name (e.g. BCA, GoPay)</label>
            <input 
              type="text" 
              className={styles.input}
              value={providerName}
              onChange={(e) => setProviderName(e.target.value)}
              placeholder="Enter provider name"
            />
          </div>
        )}

        <div className={styles.formGroup}>
          <label className={styles.label}>Order Note (Optional)</label>
          <textarea 
            className={styles.textarea}
            value={note}
            onChange={(e) => setNote(e.target.value)}
            placeholder="Leave a note for your order..."
            rows={3}
          />
        </div>

        {error && <div className={styles.errorMsg}>{error}</div>}

        <button 
          className={styles.checkoutBtn} 
          onClick={handleCheckout}
          disabled={isPending || addresses.length === 0}
        >
          {isPending ? 'Processing...' : 'Place Order'}
        </button>
      </div>

      {/* Success Modal */}
      {showSuccessModal && (
        <div className={styles.overlay}>
          <div className={styles.modal}>
            <div className={styles.successIcon}>✓</div>
            <h3 className={styles.modalTitle}>Order Successful!</h3>
            <p className={styles.modalDesc}>
              Thank you for your purchase. Your order has been placed and is now pending payment.
            </p>
            <button 
              className={styles.modalBtn} 
              onClick={() => router.push('/orders')}
            >
              View My Orders
            </button>
          </div>
        </div>
      )}
    </div>
  );
}
