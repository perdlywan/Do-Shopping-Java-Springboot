'use client';

import { useActionState } from 'react';
import { useFormStatus } from 'react-dom';
import Link from 'next/link';
import { addCategoryAction } from '@/app/actions/category';
import styles from '../../products/form.module.css';

function SubmitButton() {
  const { pending } = useFormStatus();
  return (
    <button type="submit" className={styles.submitBtn} disabled={pending}>
      {pending ? 'Saving...' : 'Save Category'}
    </button>
  );
}

export default function AddCategoryForm() {
  const [state, formAction] = useActionState(addCategoryAction, null);

  return (
    <form action={formAction} className={styles.form}>
      <div className={styles.inputGroup}>
        <label htmlFor="name" className={styles.label}>Category Name *</label>
        <input type="text" id="name" name="name" required className={styles.input} />
      </div>

      {state?.error && (
        <div className={styles.error}>
          {state.error}
        </div>
      )}

      <div className={styles.actions}>
        <Link href="/categories" className={styles.cancelBtn}>Cancel</Link>
        <SubmitButton />
      </div>
    </form>
  );
}
