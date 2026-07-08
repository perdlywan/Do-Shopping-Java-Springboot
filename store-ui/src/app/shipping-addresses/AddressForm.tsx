'use client';

import React, { useState, useTransition } from 'react';
import styles from './shipping.module.css';
import { addShippingAddress, updateShippingAddress } from '@/app/actions/shipping';

type Address = {
  id: string;
  address: string;
  city: string;
  state: string;
  country: string;
  postalCode: string;
  isDefault: boolean;
};

type AddressFormProps = {
  address?: Address | null; // If null, it's a create form. If provided, it's an edit form.
  onClose: () => void;
  onSuccess: () => void;
};

export default function AddressForm({ address, onClose, onSuccess }: AddressFormProps) {
  const [isPending, startTransition] = useTransition();
  const [error, setError] = useState<string | null>(null);

  const handleSubmit = (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    setError(null);
    const formData = new FormData(e.currentTarget);

    startTransition(async () => {
      let res;
      if (address) {
        res = await updateShippingAddress(address.id, formData);
      } else {
        res = await addShippingAddress(formData);
      }

      if (res?.error) {
        setError(res.error);
      } else if (res?.success) {
        onSuccess();
        onClose();
      }
    });
  };

  return (
    <div className={styles.modalOverlay}>
      <div className={styles.modalContent}>
        <div className={styles.modalHeader}>
          <h2 className={styles.modalTitle}>
            {address ? 'Edit Address' : 'Add New Address'}
          </h2>
          <button className={styles.closeBtn} onClick={onClose}>
            &times;
          </button>
        </div>

        <form className={styles.form} onSubmit={handleSubmit}>
          <div className={styles.formGroup}>
            <label className={`${styles.label} ${styles.required}`} htmlFor="address">Street Address</label>
            <textarea 
              id="address" 
              name="address" 
              className={`${styles.input} ${styles.textarea}`} 
              defaultValue={address?.address || ''}
              required 
            />
          </div>

          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '1rem' }}>
            <div className={styles.formGroup}>
              <label className={`${styles.label} ${styles.required}`} htmlFor="city">City</label>
              <input 
                type="text" 
                id="city" 
                name="city" 
                className={styles.input} 
                defaultValue={address?.city || ''}
                required 
              />
            </div>
            
            <div className={styles.formGroup}>
              <label className={`${styles.label} ${styles.required}`} htmlFor="state">State / Province</label>
              <input 
                type="text" 
                id="state" 
                name="state" 
                className={styles.input} 
                defaultValue={address?.state || ''}
                required 
              />
            </div>
          </div>

          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '1rem' }}>
            <div className={styles.formGroup}>
              <label className={`${styles.label} ${styles.required}`} htmlFor="country">Country</label>
              <input 
                type="text" 
                id="country" 
                name="country" 
                className={styles.input} 
                defaultValue={address?.country || ''}
                required 
              />
            </div>
            
            <div className={styles.formGroup}>
              <label className={`${styles.label} ${styles.required}`} htmlFor="postalCode">Postal Code</label>
              <input 
                type="text" 
                id="postalCode" 
                name="postalCode" 
                className={styles.input} 
                defaultValue={address?.postalCode || ''}
                required 
              />
            </div>
          </div>

          {error && <p className={styles.errorMsg}>{error}</p>}

          <button type="submit" className={styles.submitBtn} disabled={isPending}>
            {isPending ? 'Saving...' : 'Save Address'}
          </button>
        </form>
      </div>
    </div>
  );
}
