'use client';

import { useActionState } from 'react';
import Link from 'next/link';
import { useSearchParams } from 'next/navigation';
import { loginAction } from '@/app/actions/auth';
import styles from '../auth.module.css';

export default function LoginPage() {
  const [state, formAction, isPending] = useActionState(loginAction, null);
  const searchParams = useSearchParams();
  const registered = searchParams.get('registered');

  return (
    <div className={styles.authContainer}>
      <div className={styles.authCard}>
        <h1 className={styles.authTitle}>Welcome Back</h1>
        <p className={styles.authDesc}>Sign in to your account to continue shopping.</p>

        {registered && (
          <div className={styles.successAlert}>
            Registration successful! Please sign in with your new account.
          </div>
        )}

        {state?.error && (
          <div className={styles.errorAlert}>
            {state.error}
          </div>
        )}

        <form action={formAction} className={styles.authForm}>
          <div className={styles.formGroup}>
            <label htmlFor="username" className={styles.label}>Username</label>
            <input 
              type="text" 
              id="username" 
              name="username" 
              className={styles.input} 
              required 
              placeholder="Enter your username"
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
              placeholder="Enter your password"
            />
          </div>

          <button type="submit" className={styles.submitBtn} disabled={isPending}>
            {isPending ? 'Signing in...' : 'Sign In'}
          </button>
        </form>

        <p className={styles.linkText}>
          Don&apos;t have an account? 
          <Link href="/register" className={styles.link}>
            Create one
          </Link>
        </p>
      </div>
    </div>
  );
}
