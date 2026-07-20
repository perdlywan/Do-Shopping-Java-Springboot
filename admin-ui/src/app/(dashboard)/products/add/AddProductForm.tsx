'use client';

import { useActionState, useState } from 'react';
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
  const [preview, setPreview] = useState<string | null>(null);

  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (file) {
      const reader = new FileReader();
      reader.onload = () => setPreview(reader.result as string);
      reader.readAsDataURL(file);
    } else {
      setPreview(null);
    }
  };

  return (
    <form action={formAction} className={styles.form}>
      <div className={styles.inputGroup}>
        <label className={styles.label}>Product Image</label>
        <div style={{ display: 'flex', alignItems: 'flex-start', gap: '1rem' }}>
          <div style={{
            width: 120, height: 120,
            border: '2px dashed var(--border-color)',
            borderRadius: 'var(--radius-md)',
            overflow: 'hidden',
            display: 'flex', alignItems: 'center', justifyContent: 'center',
            backgroundColor: 'var(--bg-primary)',
            flexShrink: 0,
          }}>
            {preview ? (
              <img src={preview} alt="Preview" style={{ width: '100%', height: '100%', objectFit: 'cover' }} />
            ) : (
              <span style={{ color: '#9ca3af' }}><svg xmlns="http://www.w3.org/2000/svg" width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round"><path d="M23 19a2 2 0 0 1-2 2H3a2 2 0 0 1-2-2V8a2 2 0 0 1 2-2h4l2-3h6l2 3h4a2 2 0 0 1 2 2z"/><circle cx="12" cy="13" r="4"/></svg></span>
            )}
          </div>
          <div style={{ display: 'flex', flexDirection: 'column', gap: '0.5rem' }}>
            <input
              type="file"
              name="file"
              accept="image/jpeg,image/png,image/webp"
              onChange={handleFileChange}
              style={{ fontSize: '0.8rem' }}
            />
            <p style={{ fontSize: '0.75rem', color: 'var(--text-secondary)', margin: 0 }}>
              JPG, PNG or WEBP. Max 5MB.
            </p>
          </div>
        </div>
      </div>

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
