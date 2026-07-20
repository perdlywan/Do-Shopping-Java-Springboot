'use client';

import { useActionState, useState, useRef } from 'react';
import { uploadProductImageAction, deleteProductImageAction } from '@/app/actions/product';
import styles from './ProductImageUpload.module.css';

interface ProductImageUploadProps {
  productId: string;
  currentImageUrl?: string | null;
}

export default function ProductImageUpload({ productId, currentImageUrl }: ProductImageUploadProps) {
  const uploadAction = uploadProductImageAction.bind(null, productId);
  const [state, formAction, isPending] = useActionState(uploadAction, null);
  const [preview, setPreview] = useState<string | null>(null);
  const [isDeleting, setIsDeleting] = useState(false);
  const fileInputRef = useRef<HTMLInputElement>(null);

  const displayUrl = state?.success && state?.imageUrl
    ? `http://localhost:8080${state.imageUrl}`
    : currentImageUrl
      ? `http://localhost:8080${currentImageUrl}`
      : null;

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

  const handleDelete = async () => {
    if (!confirm('Are you sure you want to delete this image?')) return;
    setIsDeleting(true);
    try {
      await deleteProductImageAction(productId);
      setPreview(null);
      if (fileInputRef.current) fileInputRef.current.value = '';
    } catch (err) {
      alert('Failed to delete image.');
    } finally {
      setIsDeleting(false);
    }
  };

  return (
    <div className={styles.container}>
      <label className={styles.label}>Product Image</label>

      <div className={styles.imageArea}>
        {(preview || displayUrl) ? (
          <div className={styles.previewWrapper}>
            <img
              src={preview || displayUrl!}
              alt="Product"
              className={styles.preview}
            />
            {!preview && displayUrl && (
              <button
                type="button"
                onClick={handleDelete}
                disabled={isDeleting}
                className={styles.deleteBtn}
              >
                {isDeleting ? '...' : <svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><path d="M18 6 6 18"/><path d="m6 6 12 12"/></svg>}
              </button>
            )}
          </div>
        ) : (
          <div className={styles.placeholder}>
            <span className={styles.placeholderIcon}><svg xmlns="http://www.w3.org/2000/svg" width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round"><path d="M23 19a2 2 0 0 1-2 2H3a2 2 0 0 1-2-2V8a2 2 0 0 1 2-2h4l2-3h6l2 3h4a2 2 0 0 1 2 2z"/><circle cx="12" cy="13" r="4"/></svg></span>
            <span className={styles.placeholderText}>No image</span>
          </div>
        )}
      </div>

      <form action={formAction} className={styles.uploadForm}>
        <input
          ref={fileInputRef}
          type="file"
          name="file"
          accept="image/jpeg,image/png,image/webp"
          onChange={handleFileChange}
          className={styles.fileInput}
        />
        <button
          type="submit"
          disabled={isPending}
          className={styles.uploadBtn}
        >
          {isPending ? 'Uploading...' : 'Upload Image'}
        </button>
      </form>

      {state?.error && (
        <div className={styles.error}>{state.error}</div>
      )}
      {state?.success && (
        <div className={styles.success}>Image uploaded successfully!</div>
      )}

      <p className={styles.hint}>JPG, PNG or WEBP. Max 5MB.</p>
    </div>
  );
}
