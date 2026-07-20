import React from 'react';
import styles from './cart.module.css';
import { getShippingAddresses } from '@/app/actions/shipping';
import { cookies } from 'next/headers';
import CartView from './CartView';

export default async function CartPage() {
  const cookieStore = await cookies();
  const token = cookieStore.get('store_token')?.value;
  const isAuthenticated = !!token;

  let addresses = [];

  if (isAuthenticated) {
    const res = await getShippingAddresses();
    if (res.data) {
      addresses = res.data;
    }
  }

  return (
    <div className={`container animate-fade-in ${styles.container}`}>
      <h1 className={styles.title}>Shopping Cart</h1>
      <CartView addresses={addresses} isAuthenticated={isAuthenticated} />
    </div>
  );
}
