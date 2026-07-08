'use client';

import { useActionState } from 'react';
import { useFormStatus } from 'react-dom';
import Link from 'next/link';
import { addProductAction } from '@/app/actions/product';
import styles from '../form.module.css';

interface Category {
  id: string;
  name: string;
}

function SubmitButton() {
  const { pending } = useFormStatus();
  return (
    <button type="submit" className={styles.submitBtn} disabled={pending}>
      {pending ? 'Saving...' : 'Save Product'}
    </button>
  );
}

export default function AddProductForm({ categories }: { categories: Category[] }) {
  const [state, formAction] = useActionState(addProductAction, null);

  return (
    <form action={formAction} className={styles.form}>
      <div className={styles.inputGroup}>
        <label htmlFor="name" className={styles.label}>Product Name *</label>
        <input type="text" id="name" name="name" required className={styles.input} />
      </div>

      <div className={styles.inputGroup}>
        <label htmlFor="categoryId" className={styles.label}>Category *</label>
        <select id="categoryId" name="categoryId" required className={styles.input}>
          <option value="">-- Select Category --</option>
          {categories.map(cat => (
            <option key={cat.id} value={cat.id}>{cat.name}</option>
          ))}
        </select>
      </div>

      <div className={styles.inputGroup}>
        <label htmlFor="price" className={styles.label}>Price (IDR) *</label>
        <input type="number" id="price" name="price" required min="0" className={styles.input} />
      </div>

      <div className={styles.inputGroup}>
        <label htmlFor="stock" className={styles.label}>Stock *</label>
        <input type="number" id="stock" name="stock" required min="0" className={styles.input} />
      </div>

      <div className={styles.inputGroup}>
        <label htmlFor="description" className={styles.label}>Description</label>
        <textarea id="description" name="description" className={styles.input} />
      </div>

      {state?.error && (
        <div className={styles.error}>
          {state.error}
        </div>
      )}

      <div className={styles.actions}>
        <Link href="/products" className={styles.cancelBtn}>Cancel</Link>
        <SubmitButton />
      </div>
    </form>
  );
}
