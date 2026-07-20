'use client';

import { useActionState } from 'react';
import { editCustomerAction } from '@/app/actions/customer';
import Link from 'next/link';
import styles from '../../../products/form.module.css';

interface EditCustomerFormProps {
  id: string;
  name: string;
  phone: string;
}

export default function EditCustomerForm({ id, name, phone }: EditCustomerFormProps) {
  const [state, formAction, isPending] = useActionState(
    editCustomerAction.bind(null, id),
    null
  );

  return (
    <form action={formAction} className={styles.form}>
      {state?.error && <div className={styles.error}>{state.error}</div>}

      <div className={styles.formGroup}>
        <label htmlFor="name" className={styles.label}>
          Name *
        </label>
        <input
          type="text"
          id="name"
          name="name"
          required
          defaultValue={name}
          className={styles.input}
        />
      </div>

      <div className={styles.formGroup}>
        <label htmlFor="phone" className={styles.label}>
          Phone Number *
        </label>
        <input
          type="text"
          id="phone"
          name="phone"
          required
          defaultValue={phone || ''}
          className={styles.input}
        />
      </div>

      <div className={styles.actions}>
        <Link href={`/customers/${id}`} className={styles.cancelBtn}>
          Cancel
        </Link>
        <button type="submit" disabled={isPending} className={styles.submitBtn}>
          {isPending ? 'Updating...' : 'Update Profile'}
        </button>
      </div>
    </form>
  );
}
