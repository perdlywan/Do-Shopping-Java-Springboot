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

async function getCategories() {
  try {
    const res = await fetch('http://localhost:8080/categories?size=100', {
      next: { revalidate: 3600 } 
    });
    if (!res.ok) return [];
    const response = await res.json() as PagedResponse<Category>;
    return response.data || [];
  } catch (error) {
    return [];
  }
}

export default async function CategoriesPage() {
  const categories = await getCategories();

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
      </div>
    </div>
  );
}
