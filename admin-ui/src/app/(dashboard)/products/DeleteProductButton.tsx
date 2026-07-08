'use client';

import { useTransition, useState } from 'react';
import { deleteProductAction } from '@/app/actions/product';
import styles from './DeleteProductButton.module.css';

export default function DeleteProductButton({ id, productName }: { id: string, productName: string }) {
  const [isPending, startTransition] = useTransition();
  const [showModal, setShowModal] = useState(false);

  const handleDelete = () => {
    startTransition(async () => {
      try {
        await deleteProductAction(id);
        setShowModal(false);
        import('react-hot-toast').then(({ toast }) => toast.success('Product deleted successfully'));
      } catch (error) {
        import('react-hot-toast').then(({ toast }) => toast.error('Failed to delete product.'));
      }
    });
  };

  return (
    <>
      <button 
        onClick={() => setShowModal(true)}
        className={styles.deleteBtn}
      >
        Delete
      </button>

      {showModal && (
        <div className={styles.modalOverlay}>
          <div className={styles.modalContent}>
            <div className={styles.iconContainer}>
              <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                <path d="M3 6h18"></path>
                <path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"></path>
              </svg>
            </div>
            <h3 className={styles.modalTitle}>Delete Product</h3>
            <p className={styles.modalDescription}>
              Are you sure you want to delete <strong>"{productName}"</strong>? This action cannot be undone.
            </p>
            <div className={styles.modalActions}>
              <button 
                onClick={() => setShowModal(false)} 
                className={styles.cancelBtn}
                disabled={isPending}
              >
                Cancel
              </button>
              <button 
                onClick={handleDelete}
                className={styles.confirmBtn}
                disabled={isPending}
              >
                {isPending ? 'Deleting...' : 'Delete'}
              </button>
            </div>
          </div>
        </div>
      )}
    </>
  );
}
