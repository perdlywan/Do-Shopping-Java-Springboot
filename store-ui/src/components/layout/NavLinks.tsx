'use client';

import Link from 'next/link';
import { usePathname } from 'next/navigation';
import styles from './Navbar.module.css';

interface NavLinksProps {
  token?: string;
}

export default function NavLinks({ token }: NavLinksProps) {
  const pathname = usePathname();

  return (
    <nav className={styles.navLinks}>
      <Link 
        href="/" 
        className={`${styles.link} ${pathname === '/' ? styles.active : ''}`}
      >
        Shop
      </Link>
      <Link 
        href="/products" 
        className={`${styles.link} ${pathname === '/products' ? styles.active : ''}`}
      >
        Products
      </Link>
      <Link 
        href="/categories" 
        className={`${styles.link} ${pathname === '/categories' ? styles.active : ''}`}
      >
        Categories
      </Link>
      {token && (
        <Link 
          href="/shipping-addresses" 
          className={`${styles.link} ${pathname === '/shipping-addresses' ? styles.active : ''}`}
        >
          Addresses
        </Link>
      )}
      {token && (
        <Link 
          href="/orders" 
          className={`${styles.link} ${pathname === '/orders' ? styles.active : ''}`}
        >
          My Orders
        </Link>
      )}
    </nav>
  );
}
