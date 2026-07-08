'use client';

import Link from 'next/link';
import { usePathname } from 'next/navigation';
import { logoutAction } from '@/app/actions/auth';
import styles from './Sidebar.module.css';

const navItems = [
  { label: 'Dashboard', href: '/', icon: '📊' },
  { label: 'Orders', href: '/orders', icon: '🛒' },
  { label: 'Products', href: '/products', icon: '📦' },
  { label: 'Categories', href: '/categories', icon: '🏷️' },
  { label: 'Customers', href: '/customers', icon: '👥' },
  { label: 'Reports', href: '/reports', icon: '📈' },
];

interface SidebarProps {
  isOpen: boolean;
  closeSidebar: () => void;
}

export default function Sidebar({ isOpen, closeSidebar }: SidebarProps) {
  const pathname = usePathname();

  return (
    <aside className={`${styles.sidebar} ${isOpen ? styles.open : ''}`}>
      <div className={styles.logo}>
        <div className={styles.logoIcon}>D</div>
        Do Shopping
      </div>

      <nav className={styles.nav}>
        {navItems.map((item) => {
          const isActive = pathname === item.href;
          return (
            <Link
              key={item.href}
              href={item.href}
              className={`${styles.navItem} ${isActive ? styles.active : ''}`}
              onClick={closeSidebar}
            >
              <span className={styles.icon}>{item.icon}</span>
              {item.label}
            </Link>
          );
        })}
      </nav>

      <div className={styles.footer}>
        <button
          onClick={() => {
            closeSidebar();
            logoutAction();
          }}
          className={styles.logoutBtn}
        >
          <span className={styles.icon}>🚪</span>
          Logout
        </button>
      </div>
    </aside>
  );
}
