import Link from 'next/link';
import styles from './page.module.css';
import ProductCard from '@/components/product/ProductCard';

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

async function getProducts(categoryId?: string) {
  try {
    const url = new URL('http://localhost:8080/products');
    url.searchParams.append('page', '1');
    url.searchParams.append('size', '12');
    if (categoryId) {
      url.searchParams.append('categoryId', categoryId);
    }
    
    const res = await fetch(url.toString(), {
      next: { revalidate: 10 } 
    });
    
    if (!res.ok) return [];
    
    const response = await res.json() as PagedResponse<Product>;
    return response.data || [];
  } catch (error) {
    console.error('Failed to fetch products', error);
    return [];
  }
}

export default async function Home({ searchParams }: { searchParams?: Promise<{ categoryId?: string }> }) {
  const resolvedParams = await searchParams;
  const currentCategoryId = resolvedParams?.categoryId;
  
  const [categories, products] = await Promise.all([
    getCategories(),
    getProducts(currentCategoryId)
  ]);

  return (
    <div>
      {/* Hero Section */}
      <section className={styles.hero}>
        <div className={`container ${styles.heroContainer}`}>
          <div className={styles.heroContent}>
            <h1 className={styles.heroTitle}>
              Elevate Your <br />
              <span className={styles.highlight}>Shopping Experience</span>
            </h1>
            <p className={styles.heroDesc}>
              Discover premium products curated just for you. From tech gadgets to daily essentials, find everything you need with ease.
            </p>
            <div className={styles.heroActions}>
              <Link href="#products" className={styles.primaryBtn}>
                Shop Now
              </Link>
            </div>
          </div>
          <div className={styles.heroImage}>
            <div className={styles.imagePlaceholder}>
              🛍️ Do Shopping
            </div>
          </div>
        </div>
      </section>

      {/* Products Section */}
      <section id="products" className={styles.productsSection}>
        <div className="container">
          <div className={styles.sectionHeader}>
            <h2 className={styles.sectionTitle}>Latest Arrivals</h2>
          </div>
          
          {/* Category Filter */}
          {categories.length > 0 && (
            <div className={styles.categoryFilter}>
              <Link 
                href="/" 
                className={`${styles.categoryPill} ${!currentCategoryId ? styles.activeCategory : ''}`}
                scroll={false}
              >
                All
              </Link>
              {categories.map(cat => (
                <Link 
                  key={cat.id} 
                  href={`/?categoryId=${cat.id}`}
                  className={`${styles.categoryPill} ${currentCategoryId === cat.id ? styles.activeCategory : ''}`}
                  scroll={false}
                >
                  {cat.name}
                </Link>
              ))}
            </div>
          )}

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
              <div className={styles.viewAllContainer}>
                <Link href="/products" className={styles.secondaryBtn}>
                  View All Products
                </Link>
              </div>
            </>
          )}
        </div>
      </section>
    </div>
  );
}
