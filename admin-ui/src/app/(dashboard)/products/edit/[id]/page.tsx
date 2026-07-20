import { cookies } from 'next/headers';
import Link from 'next/link';
import { notFound } from 'next/navigation';
import styles from '../../form.module.css';
import EditProductForm from './EditProductForm';

interface PagedResponse<T> {
  statusCode: number;
  data: T[];
}

interface DataResponse<T> {
  statusCode: number;
  data: T;
}

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
  imageUrl?: string;
}

async function getCategories() {
  try {
    const cookieStore = await cookies();
    const token = cookieStore.get('token')?.value;

    const headers: HeadersInit = {};
    if (token) {
      headers['Authorization'] = `Bearer ${token}`;
    }

    const res = await fetch('http://localhost:8080/categories?size=100', {
      headers
    });
    if (!res.ok) return [];
    
    const response = await res.json() as PagedResponse<Category>;
    return response.data || [];
  } catch (error) {
    return [];
  }
}

async function getProduct(id: string) {
  try {
    const cookieStore = await cookies();
    const token = cookieStore.get('token')?.value;

    const headers: HeadersInit = {};
    if (token) {
      headers['Authorization'] = `Bearer ${token}`;
    }

    const res = await fetch(`http://localhost:8080/products/${id}`, {
      headers
    });
    
    if (!res.ok) return null;
    
    const response = await res.json() as DataResponse<Product>;
    return response.data;
  } catch (error) {
    return null;
  }
}

export default async function EditProductPage({ params }: { params: Promise<{ id: string }> }) {
  const resolvedParams = await params;
  const { id } = resolvedParams;

  const [categories, product] = await Promise.all([
    getCategories(),
    getProduct(id)
  ]);

  if (!product) {
    notFound();
  }

  return (
    <div className={styles.container}>
      <div className={styles.header}>
        <h1 className={styles.title}>Edit Product</h1>
      </div>

      <div className={styles.card} style={{ marginTop: '1.5rem' }}>
        <EditProductForm categories={categories} product={product} />
      </div>
    </div>
  );
}
