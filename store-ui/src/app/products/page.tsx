import Link from 'next/link';
import styles from './products.module.css';
import ProductCard from '@/components/product/ProductCard';

interface Product {
  id: string;
  categoryId: string;
  name: string;
  price: number;
  stock: number;
  description: string;
  imageUrl?: string;
}

interface PagedResponse<T> {
  statusCode: number;
  data: T[];
  totalElements: number;
  totalPages: number;
}

async function getProducts(page: number, categoryId?: string) {
  try {
    const url = new URL('http://localhost:8080/products');
    url.searchParams.append('page', page.toString());
    url.searchParams.append('size', '12');
    if (categoryId) {
      url.searchParams.append('categoryId', categoryId);
    }
    
    const res = await fetch(url.toString(), {
      next: { revalidate: 10 } 
    });
    
    if (!res.ok) return null;
    
    return await res.json() as PagedResponse<Product>;
  } catch (error) {
    console.error('Failed to fetch products', error);
    return null;
  }
}

export default async function ProductsPage({ searchParams }: { searchParams?: Promise<{ page?: string; categoryId?: string }> }) {
  const resolvedParams = await searchParams;
  const currentPage = Number(resolvedParams?.page) || 1;
  const categoryId = resolvedParams?.categoryId;
  
  const response = await getProducts(currentPage, categoryId);
  const products = response?.data || [];
  const totalPages = response?.totalPages || 1;

  return (
    <div className={styles.pageContainer}>
      <div className="container">
        <div className={styles.pageHeader}>
          <h1 className={styles.pageTitle}>All Products</h1>
        </div>

        {products.length === 0 ? (
          <div className={styles.emptyState}>
            <p>No products available at the moment.</p>
          </div>
        ) : (
          <>
            <div className={styles.productGrid}>
              {products.map((product) => (
                <ProductCard key={product.id} product={product} />
              ))}
            </div>

            {totalPages > 1 && (
              <div className={styles.pagination}>
                {currentPage > 1 ? (
                  <Link href={`/products?page=${currentPage - 1}${categoryId ? `&categoryId=${categoryId}` : ''}`} className={styles.pageBtn}>
                    Previous
                  </Link>
                ) : (
                  <button className={styles.pageBtn} disabled>Previous</button>
                )}
                
                <span className={styles.pageInfo}>
                  Page {currentPage} of {totalPages}
                </span>

                {currentPage < totalPages ? (
                  <Link href={`/products?page=${currentPage + 1}${categoryId ? `&categoryId=${categoryId}` : ''}`} className={styles.pageBtn}>
                    Next
                  </Link>
                ) : (
                  <button className={styles.pageBtn} disabled>Next</button>
                )}
              </div>
            )}
          </>
        )}
      </div>
    </div>
  );
}
