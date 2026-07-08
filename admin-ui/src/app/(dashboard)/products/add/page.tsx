import { cookies } from 'next/headers';
import Link from 'next/link';
import styles from '../form.module.css';
import AddProductForm from './AddProductForm';

// PagedResponseDTO format
interface PagedResponse<T> {
  statusCode: number;
  data: T[];
}

interface Category {
  id: string;
  name: string;
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
    console.error(error);
    return [];
  }
}

export default async function AddProductPage() {
  const categories = await getCategories();

  return (
    <div className={styles.container}>
      <div className={styles.header}>
        <h1 className={styles.title}>Add New Product</h1>
      </div>

      <div className={styles.card}>
        <AddProductForm categories={categories} />
      </div>
    </div>
  );
}
