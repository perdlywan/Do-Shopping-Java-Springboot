import Link from 'next/link';
import { cookies } from 'next/headers';
import styles from './Navbar.module.css';
import CartLink from './CartLink';
import NavLinks from './NavLinks';
import LogoutButton from './LogoutButton';

export default async function Navbar() {
  const cookieStore = await cookies();
  const token = cookieStore.get('store_token')?.value;

  return (
    <header className={styles.header}>
      <div className={`container ${styles.navContainer}`}>
        <Link href="/" className={styles.logo}>
          <div className={styles.logoIcon}>D</div>
          <span className={styles.logoText}>Do Shopping</span>
        </Link>

        <NavLinks token={token} />

        <div className={styles.actions}>
          <CartLink />
          {token ? (
            <LogoutButton />
          ) : (
            <Link href="/login" className={styles.loginBtn}>
              Sign In
            </Link>
          )}
        </div>
      </div>
    </header>
  );
}
