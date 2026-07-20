'use client';

import React, { useState } from 'react';
import Link from 'next/link';
import { useCart } from '@/context/CartContext';
import styles from '@/app/page.module.css';

export interface Product {
  id: string;
  categoryId: string;
  name: string;
  price: number;
  stock: number;
  description: string;
  imageUrl?: string;
}

interface ProductCardProps {
  product: Product;
}

export default function ProductCard({ product }: ProductCardProps) {
  const { addToCart } = useCart();
  const [quantity, setQuantity] = useState(1);

  const handleAddToCart = (e: React.MouseEvent) => {
    e.preventDefault(); // Prevent navigating to the product details page
    addToCart({
      productId: product.id,
      name: product.name,
      price: product.price,
      quantity: quantity,
      maxStock: product.stock,
      imageUrl: product.imageUrl
    });
    setQuantity(1); // Reset to 1 after adding
  };

  const handleQtyChange = (e: React.MouseEvent, change: number) => {
    e.preventDefault(); // Prevent navigating
    setQuantity(q => Math.max(1, Math.min(product.stock, q + change)));
  };

  return (
    <Link href={`/products/${product.id}`} className={styles.productCard}>
      <div className={styles.productImageWrapper}>
        {product.imageUrl ? (
          <img
            src={`http://localhost:8080${product.imageUrl}`}
            alt={product.name}
            className={styles.productImage}
          />
        ) : (
          <div className={styles.productImagePlaceholder}>
            <svg xmlns="http://www.w3.org/2000/svg" width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round"><path d="m7.5 4.27 9 5.15"/><path d="M21 8a2 2 0 0 0-1-1.73l-7-4a2 2 0 0 0-2 0l-7 4A2 2 0 0 0 3 8v8a2 2 0 0 0 1 1.73l7 4a2 2 0 0 0 2 0l7-4A2 2 0 0 0 21 16Z"/><path d="m3.3 7 8.7 5 8.7-5"/><path d="M12 22V12"/></svg>
          </div>
        )}
        {product.stock <= 5 && product.stock > 0 && (
          <span className={styles.badgeWarning}>Low Stock</span>
        )}
        {product.stock === 0 && (
          <span className={styles.badgeDanger}>Out of Stock</span>
        )}
      </div>
      <div className={styles.productInfo}>
        <h3 className={styles.productName}>{product.name}</h3>
        <p className={styles.productDesc}>{product.description}</p>
        <div className={styles.productFooter}>
          <span className={styles.productPrice}>
            {new Intl.NumberFormat('id-ID', { 
              style: 'currency', 
              currency: 'IDR',
              maximumFractionDigits: 0
            }).format(product.price)}
          </span>
          <div className={styles.actionRow} onClick={e => e.preventDefault()}>
            <div className={styles.qtySelector}>
              <button 
                onClick={(e) => handleQtyChange(e, -1)} 
                disabled={quantity <= 1 || product.stock === 0}
              >
                -
              </button>
              <span>{product.stock === 0 ? 0 : quantity}</span>
              <button 
                onClick={(e) => handleQtyChange(e, 1)} 
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
              Add
            </button>
          </div>
        </div>
      </div>
    </Link>
  );
}
