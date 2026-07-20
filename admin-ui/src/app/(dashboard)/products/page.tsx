import Link from 'next/link';
import DeleteProductButton from './DeleteProductButton';
import styles from './page.module.css';

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
  pageNumber: number;
  pageSize: number;
  totalElements: number;
  totalPages: number;
  isLast: boolean;
}

async function getProducts(page: number) {
  try {
    const res = await fetch(`http://localhost:8080/products?page=${page}&size=10`, {
      cache: 'no-store' // Always fetch the latest data for admin dashboard
    });
    
    if (!res.ok) {
      throw new Error(`Failed to fetch products: ${res.status}`);
    }
    
    return res.json() as Promise<PagedResponse<Product>>;
  } catch (error) {
    console.error(error);
    return null;
  }
}
export default async function ProductsPage({
  searchParams,
}: {
  searchParams?: Promise<{ page?: string }>;
}) {
  const resolvedParams = await searchParams;
  const currentPage = Number(resolvedParams?.page) || 1;
  const response = await getProducts(currentPage);
  const products = response?.data || [];
  const totalPages = response?.totalPages || 1;

  return (
    <div className={styles.container}>
      <div className={styles.header}>
        <h1 className={styles.title}>Products</h1>
        <Link href="/products/add" className={styles.addButton}>+ Add Product</Link>
      </div>

      {!response && (
        <div className={styles.error}>
          Failed to fetch products from server. Ensure the Spring Boot backend is running on port 8080.
        </div>
      )}

      <div className={styles.card}>
        <table className={styles.table}>
          <thead>
            <tr>
              <th>ID</th>
              <th>Image</th>
              <th>Name</th>
              <th>Price</th>
              <th>Stock</th>
              <th>Description</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            {products.length === 0 ? (
              <tr>
                <td colSpan={7} style={{ textAlign: 'center', padding: '2rem' }}>
                  No products available.
                </td>
              </tr>
            ) : (
              products.map((product) => (
                <tr key={product.id}>
                  <td>{product.id.substring(0, 8)}...</td>
                  <td>
                    {product.imageUrl ? (
                      <img
                        src={`http://localhost:8080${product.imageUrl}`}
                        alt={product.name}
                        style={{ width: 48, height: 48, objectFit: 'cover', borderRadius: 4 }}
                      />
                    ) : (
                      <span style={{ color: 'var(--text-secondary)', fontSize: '0.8rem' }}>No image</span>
                    )}
                  </td>
                  <td>{product.name}</td>
                  <td className={styles.price}>
                    {new Intl.NumberFormat('id-ID', { style: 'currency', currency: 'IDR' }).format(product.price)}
                  </td>
                  <td>{product.stock}</td>
                  <td style={{ maxWidth: '200px', whiteSpace: 'nowrap', overflow: 'hidden', textOverflow: 'ellipsis' }}>
                    {product.description}
                  </td>
                  <td>
                    <Link href={`/products/edit/${product.id}`} style={{ color: 'var(--accent-primary)', marginRight: '8px', textDecoration: 'none' }}>Edit</Link>
                    <DeleteProductButton id={product.id} productName={product.name} />
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
              href={`/products?page=${currentPage - 1}`}
              className={`${styles.pageBtn} ${currentPage <= 1 ? styles.disabled : ''}`}
            >
              Previous
            </Link>
            
            <span className={styles.pageInfo}>
              Page {currentPage} of {totalPages}
            </span>

            <Link 
              href={`/products?page=${currentPage + 1}`}
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
