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
      <span className={styles.cartIcon}>🛒</span>
      <span className={styles.cartBadge}>
        {mounted ? cartTotalQuantity : 0}
      </span>
    </Link>
  );
}
