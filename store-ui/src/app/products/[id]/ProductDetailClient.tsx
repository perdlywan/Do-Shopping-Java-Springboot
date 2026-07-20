'use client';

import React, { useState } from 'react';
import { useCart } from '@/context/CartContext';
import styles from './product-detail.module.css';
import Link from 'next/link';

interface Product {
  id: string;
  categoryId: string;
  name: string;
  price: number;
  stock: number;
  description: string;
  imageUrl?: string;
}

export default function ProductDetailClient({ product }: { product: Product }) {
  const { addToCart } = useCart();
  const [quantity, setQuantity] = useState(1);

  const handleAddToCart = () => {
    addToCart({
      productId: product.id,
      name: product.name,
      price: product.price,
      quantity: quantity,
      maxStock: product.stock,
      imageUrl: product.imageUrl
    });
    setQuantity(1);
  };

  const incrementQty = () => {
    if (quantity < product.stock) {
      setQuantity(q => q + 1);
    }
  };

  const decrementQty = () => {
    if (quantity > 1) {
      setQuantity(q => q - 1);
    }
  };

  let stockBadge;
  if (product.stock === 0) {
    stockBadge = <span className={`${styles.badge} ${styles.outOfStock}`}>Out of Stock</span>;
  } else if (product.stock <= 5) {
    stockBadge = <span className={`${styles.badge} ${styles.lowStock}`}>Low Stock ({product.stock} left)</span>;
  } else {
    stockBadge = <span className={`${styles.badge} ${styles.inStock}`}>In Stock</span>;
  }

  return (
    <div className={styles.container}>
      <div className={styles.breadcrumb}>
        <Link href="/" className={styles.breadcrumbLink}>Home</Link>
        {' > '}
        <Link href="/products" className={styles.breadcrumbLink}>Products</Link>
        {' > '}
        <span style={{ color: 'var(--text-primary)' }}>{product.name}</span>
      </div>

      <div className={styles.productWrapper}>
        <div className={styles.imageSection}>
          {product.imageUrl ? (
            <img 
              src={`http://localhost:8080${product.imageUrl}`} 
              alt={product.name} 
              className={styles.image}
            />
          ) : (
            <div className={styles.imagePlaceholder}>
              <svg xmlns="http://www.w3.org/2000/svg" width="64" height="64" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round"><path d="m7.5 4.27 9 5.15"/><path d="M21 8a2 2 0 0 0-1-1.73l-7-4a2 2 0 0 0-2 0l-7 4A2 2 0 0 0 3 8v8a2 2 0 0 0 1 1.73l7 4a2 2 0 0 0 2 0l7-4A2 2 0 0 0 21 16Z"/><path d="m3.3 7 8.7 5 8.7-5"/><path d="M12 22V12"/></svg>
              <span>No Image Available</span>
            </div>
          )}
        </div>

        <div className={styles.infoSection}>
          <h1 className={styles.title}>{product.name}</h1>
          <div className={styles.price}>
            {new Intl.NumberFormat('id-ID', { 
              style: 'currency', 
              currency: 'IDR',
              maximumFractionDigits: 0
            }).format(product.price)}
          </div>
          
          {stockBadge}

          <div className={styles.description}>
            {product.description || "No description provided."}
          </div>

          <div className={styles.actionSection}>
            <span className={styles.qtyLabel}>Quantity</span>
            <div className={styles.actionRow}>
              <div className={styles.qtySelector}>
                <button 
                  className={styles.qtyBtn} 
                  onClick={decrementQty}
                  disabled={quantity <= 1 || product.stock === 0}
                >
                  -
                </button>
                <div className={styles.qtyValue}>{product.stock === 0 ? 0 : quantity}</div>
                <button 
                  className={styles.qtyBtn} 
                  onClick={incrementQty}
                  disabled={quantity >= product.stock || product.stock === 0}
                >
                  +
                </button>
              </div>
              <button 
                className={styles.addToCartBtn}
                disabled={product.stock === 0}
                onClick={handleAddToCart}
              >
                <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><circle cx="8" cy="21" r="1"/><circle cx="19" cy="21" r="1"/><path d="M2.05 2.05h2l2.66 12.42a2 2 0 0 0 2 1.58h9.78a2 2 0 0 0 1.95-1.57l1.65-7.43H5.12"/></svg>
                Add to Cart
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
