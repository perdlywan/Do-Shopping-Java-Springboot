'use client';

import Link from 'next/link';
import { useCart } from '@/context/CartContext';
import styles from './Navbar.module.css';
import { useEffect, useState } from 'react';

export default function CartLink() {
  const { cartTotalQuantity } = useCart();
  const [mounted, setMounted] = useState(false);

  useEffect(() => {
    setMounted(true);
  }, []);

  return (
    <Link href="/cart" className={styles.cartBtn}>
      <span className={styles.cartIcon}><svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><circle cx="8" cy="21" r="1"/><circle cx="19" cy="21" r="1"/><path d="M2.05 2.05h2l2.66 12.42a2 2 0 0 0 2 1.58h9.78a2 2 0 0 0 1.95-1.57l1.65-7.43H5.12"/></svg></span>
      <span className={styles.cartBadge}>
        {mounted ? cartTotalQuantity : 0}
      </span>
    </Link>
  );
}
