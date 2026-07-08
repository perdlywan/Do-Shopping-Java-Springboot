import { cookies } from 'next/headers';
import { notFound } from 'next/navigation';
import styles from '../../../products/form.module.css';
import EditCategoryForm from './EditCategoryForm';

interface DataResponse<T> {
  statusCode: number;
  data: T;
}

interface Category {
  id: string;
  name: string;
}

async function getCategory(id: string) {
  try {
    const cookieStore = await cookies();
    const token = cookieStore.get('token')?.value;

    const headers: HeadersInit = {};
    if (token) {
      headers['Authorization'] = `Bearer ${token}`;
    }

    const res = await fetch(`http://localhost:8080/categories/${id}`, {
      headers
    });
    
    if (!res.ok) return null;
    
    const response = await res.json() as DataResponse<Category>;
    return response.data;
  } catch (error) {
    return null;
  }
}

export default async function EditCategoryPage({ params }: { params: Promise<{ id: string }> }) {
  const resolvedParams = await params;
  const { id } = resolvedParams;

  const category = await getCategory(id);

  if (!category) {
    notFound();
  }

  return (
    <div className={styles.container}>
      <div className={styles.header}>
        <h1 className={styles.title}>Edit Category</h1>
      </div>

      <div className={styles.card}>
        <EditCategoryForm category={category} />
      </div>
    </div>
  );
}
