'use client';

import React from 'react';
import styles from './cart.module.css';
import { useCart } from '@/context/CartContext';
import Link from 'next/link';
import CheckoutForm from './CheckoutForm';

interface Address {
  id: string;
  address: string;
  city: string;
  state: string;
  country: string;
  postalCode: string;
  isDefault: boolean;
}

interface CartViewProps {
  addresses: Address[];
  isAuthenticated: boolean;
}

export default function CartView({ addresses, isAuthenticated }: CartViewProps) {
  const { cartItems, removeFromCart, updateQuantity } = useCart();
  const [mounted, setMounted] = React.useState(false);

  React.useEffect(() => {
    setMounted(true);
  }, []);

  if (!mounted) return null; // Prevent hydration mismatch

  if (cartItems.length === 0) {
    return (
      <div className={styles.emptyState}>
        <div className={styles.emptyIcon}><svg xmlns="http://www.w3.org/2000/svg" width="64" height="64" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round"><circle cx="8" cy="21" r="1"/><circle cx="19" cy="21" r="1"/><path d="M2.05 2.05h2l2.66 12.42a2 2 0 0 0 2 1.58h9.78a2 2 0 0 0 1.95-1.57l1.65-7.43H5.12"/></svg></div>
        <h2 className={styles.emptyTitle}>Your cart is empty</h2>
        <p className={styles.emptyDesc}>Looks like you haven't added any items to your cart yet.</p>
        <Link href="/" className={styles.shopBtn}>
          Start Shopping
        </Link>
      </div>
    );
  }

  return (
    <div className={styles.cartLayout}>
      <div className={styles.cartItems}>
        {cartItems.map((item) => (
          <div key={item.productId} className={styles.cartItem}>
            <div className={styles.itemImage}>
              {item.imageUrl ? (
                <img 
                  src={`http://localhost:8080${item.imageUrl}`} 
                  alt={item.name} 
                  style={{ width: '100%', height: '100%', objectFit: 'contain' }}
                />
              ) : (
                <svg xmlns="http://www.w3.org/2000/svg" width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round"><path d="m7.5 4.27 9 5.15"/><path d="M21 8a2 2 0 0 0-1-1.73l-7-4a2 2 0 0 0-2 0l-7 4A2 2 0 0 0 3 8v8a2 2 0 0 0 1 1.73l7 4a2 2 0 0 0 2 0l7-4A2 2 0 0 0 21 16Z"/><path d="m3.3 7 8.7 5 8.7-5"/><path d="M12 22V12"/></svg>
              )}
            </div>
            <div className={styles.itemDetails}>
              <h3 className={styles.itemName}>{item.name}</h3>
              <div className={styles.itemPrice}>
                {new Intl.NumberFormat('id-ID', { 
                  style: 'currency', currency: 'IDR', maximumFractionDigits: 0
                }).format(item.price)}
              </div>
            </div>
            <div className={styles.itemActions}>
              <div className={styles.quantityControl}>
                <button 
                  className={styles.qtyBtn} 
                  onClick={() => updateQuantity(item.productId, item.quantity - 1)}
                  disabled={item.quantity <= 1}
                >
                  -
                </button>
                <input 
                  type="number" 
                  className={styles.qtyInput} 
                  value={item.quantity} 
                  onChange={(e) => updateQuantity(item.productId, parseInt(e.target.value) || 1)}
                  min="1"
                  max={item.maxStock}
                />
                <button 
                  className={styles.qtyBtn} 
                  onClick={() => updateQuantity(item.productId, item.quantity + 1)}
                  disabled={item.quantity >= item.maxStock}
                >
                  +
                </button>
              </div>
              <button 
                className={styles.removeBtn} 
                onClick={() => removeFromCart(item.productId)}
                title="Remove item"
              >
                <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><polyline points="3 6 5 6 21 6"/><path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"/></svg>
              </button>
            </div>
          </div>
        ))}
      </div>

      <div className={styles.checkoutSection}>
        {isAuthenticated ? (
          <CheckoutForm addresses={addresses} />
        ) : (
          <div className={styles.summaryPanel}>
            <h2 className={styles.summaryTitle}>Sign in to Checkout</h2>
            <p style={{ color: 'var(--text-secondary)', marginBottom: '1.5rem' }}>
              You need an account to place an order and track your shipping.
            </p>
            <Link href="/login" className={styles.checkoutBtn} style={{ display: 'block', textAlign: 'center', textDecoration: 'none' }}>
              Sign In
            </Link>
          </div>
        )}
      </div>
    </div>
  );
}
