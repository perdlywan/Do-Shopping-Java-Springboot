import Link from 'next/link';
import { cookies } from 'next/headers';
import DeleteCategoryButton from './DeleteCategoryButton';
import styles from '../products/page.module.css';

interface Category {
  id: string;
  name: string;
}

interface PagedResponse<T> {
  statusCode: number;
  data: T[];
  pageNumber: number;
  pageSize: number;
  totalElements: number;
  totalPages: number;
  isLast: boolean;
}

async function getCategories(page: number) {
  try {
    const cookieStore = await cookies();
    const token = cookieStore.get('token')?.value;

    const headers: HeadersInit = {};
    if (token) {
      headers['Authorization'] = `Bearer ${token}`;
    }

    const res = await fetch(`http://localhost:8080/categories?page=${page}&size=10`, {
      cache: 'no-store',
      headers
    });
    
    if (!res.ok) {
      throw new Error(`Failed to fetch categories: ${res.status}`);
    }
    
    return res.json() as Promise<PagedResponse<Category>>;
  } catch (error) {
    console.error(error);
    return null;
  }
}

export default async function CategoriesPage({
  searchParams,
}: {
  searchParams?: Promise<{ page?: string }>;
}) {
  const resolvedParams = await searchParams;
  const currentPage = Number(resolvedParams?.page) || 1;
  const response = await getCategories(currentPage);
  const categories = response?.data || [];
  const totalPages = response?.totalPages || 1;

  return (
    <div className={styles.container}>
      <div className={styles.header}>
        <h1 className={styles.title}>Categories</h1>
        <Link href="/categories/add" className={styles.addButton}>+ Add Category</Link>
      </div>

      {!response && (
        <div className={styles.error}>
          Failed to fetch categories from server. Ensure the Spring Boot backend is running on port 8080.
        </div>
      )}

      <div className={styles.card}>
        <table className={styles.table}>
          <thead>
            <tr>
              <th>ID</th>
              <th>Name</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            {categories.length === 0 ? (
              <tr>
                <td colSpan={3} style={{ textAlign: 'center', padding: '2rem' }}>
                  No categories available.
                </td>
              </tr>
            ) : (
              categories.map((category) => (
                <tr key={category.id}>
                  <td>{category.id.substring(0, 8)}...</td>
                  <td>{category.name}</td>
                  <td>
                    <Link href={`/categories/edit/${category.id}`} style={{ color: 'var(--accent-primary)', marginRight: '8px', textDecoration: 'none' }}>Edit</Link>
                    <DeleteCategoryButton id={category.id} categoryName={category.name} />
                  </td>
                </tr>
              ))
            )}
          </tbody>
        </table>

        {/* Pagination UI */}
        {totalPages > 1 && (
          <div className={styles.pagination}>
            <Link 
              href={`/categories?page=${currentPage - 1}`}
              className={`${styles.pageBtn} ${currentPage <= 1 ? styles.disabled : ''}`}
            >
              Previous
            </Link>
            
            <span className={styles.pageInfo}>
              Page {currentPage} of {totalPages}
            </span>

            <Link 
              href={`/categories?page=${currentPage + 1}`}
              className={`${styles.pageBtn} ${currentPage >= totalPages ? styles.disabled : ''}`}
            >
              Next
            </Link>
          </div>
        )}
      </div>
    </div>
  );
}
