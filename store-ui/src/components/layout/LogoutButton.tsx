'use client';

import { useCart } from '@/context/CartContext';
import styles from './Navbar.module.css';
import { logoutAction } from '@/app/actions/auth';

export default function LogoutButton() {
  const { clearCart } = useCart();

  const handleLogout = async () => {
    // Clear the cart on the client side
    clearCart();
    
    // Proceed with the server action to remove the token cookie and redirect
    await logoutAction();
  };

  return (
    <button onClick={handleLogout} className={styles.loginBtn}>
      Sign Out
    </button>
  );
}
