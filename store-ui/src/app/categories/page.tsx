import Link from 'next/link';
import styles from './categories.module.css';

interface Category {
  id: string;
  name: string;
}

interface PagedResponse<T> {
  statusCode: number;
  data: T[];
  totalElements: number;
  totalPages: number;
}

async function getCategories(page: number) {
  try {
    const res = await fetch(`http://localhost:8080/categories?page=${page}&size=12`, {
      next: { revalidate: 3600 } 
    });
    if (!res.ok) return null;
    return await res.json() as PagedResponse<Category>;
  } catch (error) {
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
    <div className={styles.pageContainer}>
      <div className="container">
        <div className={styles.pageHeader}>
          <h1 className={styles.pageTitle}>Shop by Category</h1>
          <p className={styles.pageDesc}>Explore our wide range of products organized by category.</p>
        </div>

        <div className={styles.categoryGrid}>
          {categories.map((category) => (
            <Link 
              key={category.id} 
              href={`/products?categoryId=${category.id}`} 
              className={styles.categoryCard}
            >
              <h2 className={styles.categoryName}>{category.name}</h2>
              <span className={styles.categoryAction}>Browse Products &rarr;</span>
            </Link>
          ))}
        </div>

        {totalPages > 1 && (
          <div className={styles.pagination}>
            {currentPage > 1 ? (
              <Link href={`/categories?page=${currentPage - 1}`} className={styles.pageBtn}>
                Previous
              </Link>
            ) : (
              <button className={styles.pageBtn} disabled>Previous</button>
            )}
            
            <span className={styles.pageInfo}>
              Page {currentPage} of {totalPages}
            </span>

            {currentPage < totalPages ? (
              <Link href={`/categories?page=${currentPage + 1}`} className={styles.pageBtn}>
                Next
              </Link>
            ) : (
              <button className={styles.pageBtn} disabled>Next</button>
            )}
          </div>
        )}
      </div>
    </div>
  );
}
