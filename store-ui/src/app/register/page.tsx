'use client';

import { useActionState } from 'react';
import Link from 'next/link';
import { registerAction } from '@/app/actions/auth';
import styles from '../auth.module.css';

export default function RegisterPage() {
  const [state, formAction, isPending] = useActionState(registerAction, null);

  return (
    <div className={styles.authContainer}>
      <div className={styles.authCard} style={{ maxWidth: '500px' }}>
        <h1 className={styles.authTitle}>Create Account</h1>
        <p className={styles.authDesc}>Join Do Shopping to manage your orders.</p>

        {state?.error && (
          <div className={styles.errorAlert}>
            {state.error}
          </div>
        )}

        <form action={formAction} className={styles.authForm}>
          <div className={styles.formGroup}>
            <label htmlFor="name" className={styles.label}>Full Name</label>
            <input 
              type="text" 
              id="name" 
              name="name" 
              className={styles.input} 
              required 
              placeholder="e.g. John Doe"
            />
          </div>

          <div className={styles.formRow}>
            <div className={styles.formGroup}>
              <label htmlFor="username" className={styles.label}>Username</label>
              <input 
                type="text" 
                id="username" 
                name="username" 
                className={styles.input} 
                required 
                placeholder="Unique username"
              />
            </div>
            
            <div className={styles.formGroup}>
              <label htmlFor="password" className={styles.label}>Password</label>
              <input 
                type="password" 
                id="password" 
                name="password" 
                className={styles.input} 
                required 
                placeholder="Secret password"
              />
            </div>
          </div>

          <div className={styles.formGroup}>
            <label htmlFor="email" className={styles.label}>Email Address</label>
            <input 
              type="email" 
              id="email" 
              name="email" 
              className={styles.input} 
              required 
              placeholder="john@example.com"
              pattern="[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}"
              title="Please enter a valid email address (e.g. name@domain.com)"
            />
          </div>

          <div className={styles.formGroup}>
            <label htmlFor="phone" className={styles.label}>Phone Number</label>
            <input 
              type="tel" 
              id="phone" 
              name="phone" 
              className={styles.input} 
              required 
              placeholder="e.g. 081234567890"
            />
          </div>

          <button type="submit" className={styles.submitBtn} disabled={isPending}>
            {isPending ? 'Creating Account...' : 'Sign Up'}
          </button>
        </form>

        <p className={styles.linkText}>
          Already have an account? 
          <Link href="/login" className={styles.link}>
            Sign In
          </Link>
        </p>
      </div>
    </div>
  );
}
