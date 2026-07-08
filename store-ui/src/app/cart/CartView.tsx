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
        <div className={styles.emptyIcon}>🛒</div>
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
              📦
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
                🗑️
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
