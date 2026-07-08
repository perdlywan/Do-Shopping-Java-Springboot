'use client';

import { useActionState } from 'react';
import { useFormStatus } from 'react-dom';
import Link from 'next/link';
import { editProductAction } from '@/app/actions/product';
import styles from '../../form.module.css';

interface Category {
  id: string;
  name: string;
}

interface Product {
  id: string;
  categoryId: string;
  name: string;
  price: number;
  stock: number;
  description: string;
}

function SubmitButton() {
  const { pending } = useFormStatus();
  return (
    <button type="submit" className={styles.submitBtn} disabled={pending}>
      {pending ? 'Saving...' : 'Save Changes'}
    </button>
  );
}

export default function EditProductForm({ categories, product }: { categories: Category[], product: Product }) {
  const updateProductWithId = editProductAction.bind(null, product.id);
  const [state, formAction] = useActionState(updateProductWithId, null);

  return (
    <form action={formAction} className={styles.form}>
      <div className={styles.inputGroup}>
        <label htmlFor="name" className={styles.label}>Product Name *</label>
        <input type="text" id="name" name="name" defaultValue={product.name} required className={styles.input} />
      </div>

      <div className={styles.inputGroup}>
        <label htmlFor="categoryId" className={styles.label}>Category *</label>
        <select id="categoryId" name="categoryId" defaultValue={(product as any).category_id || product.categoryId} required className={styles.input}>
          <option value="">-- Select Category --</option>
          {categories.map(cat => (
            <option key={cat.id} value={cat.id}>{cat.name}</option>
          ))}
        </select>
      </div>

      <div className={styles.inputGroup}>
        <label htmlFor="price" className={styles.label}>Price (IDR) *</label>
        <input type="number" id="price" name="price" defaultValue={product.price} required min="0" className={styles.input} />
      </div>

      <div className={styles.inputGroup}>
        <label htmlFor="stock" className={styles.label}>Stock *</label>
        <input type="number" id="stock" name="stock" defaultValue={product.stock} required min="0" className={styles.input} />
      </div>

      <div className={styles.inputGroup}>
        <label htmlFor="description" className={styles.label}>Description</label>
        <textarea id="description" name="description" defaultValue={product.description} className={styles.input} />
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
