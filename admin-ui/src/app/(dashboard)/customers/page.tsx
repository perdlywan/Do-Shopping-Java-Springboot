import Link from 'next/link';
import { cookies } from 'next/headers';
import styles from '../products/page.module.css';

interface Customer {
  id: string;
  username: string;
  name: string;
  email: string;
  totalOrders: number;
  totalSpent: number;
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

async function getCustomers(page: number) {
  try {
    const cookieStore = await cookies();
    const token = cookieStore.get('token')?.value;

    const headers: HeadersInit = {};
    if (token) {
      headers['Authorization'] = `Bearer ${token}`;
    }

    const res = await fetch(`http://localhost:8080/customers?page=${page}&size=10`, {
      cache: 'no-store',
      headers
    });

    if (!res.ok) {
      throw new Error(`Failed to fetch customers: ${res.status}`);
    }

    return res.json() as Promise<PagedResponse<Customer>>;
  } catch (error) {
    console.error(error);
    return null;
  }
}

export default async function CustomersPage({
  searchParams,
}: {
  searchParams?: Promise<{ page?: string }>;
}) {
  const resolvedParams = await searchParams;
  const currentPage = Number(resolvedParams?.page) || 1;
  const response = await getCustomers(currentPage);
  const customers = response?.data || [];
  const totalPages = response?.totalPages || 1;

  const formatCurrency = (amount: number) => {
    return new Intl.NumberFormat('id-ID', {
      style: 'currency',
      currency: 'IDR',
      minimumFractionDigits: 0
    }).format(amount);
  };

  return (
    <div className={styles.container}>
      <div className={styles.header}>
        <h1 className={styles.title}>Customers</h1>
      </div>

      {!response && (
        <div className={styles.error}>
          Failed to fetch customers from server. Ensure the Spring Boot backend is running.
        </div>
      )}

      <div className={styles.card}>
        <table className={styles.table}>
          <thead>
            <tr>
              <th>Name</th>
              <th>Username</th>
              <th>Email</th>
              <th>Total Orders</th>
              <th>Total Spent</th>
            </tr>
          </thead>
          <tbody>
            {customers.length === 0 ? (
              <tr>
                <td colSpan={6} style={{ textAlign: 'center', padding: '2rem' }}>
                  No customers available.
                </td>
              </tr>
            ) : (
              customers.map((customer) => (
                <tr key={customer.id}>
                  <td>{customer.name}</td>
                  <td>{customer.username}</td>
                  <td>{customer.email}</td>
                  <td>{customer.totalOrders}</td>
                  <td className={styles.price}>{formatCurrency(customer.totalSpent)}</td>
                </tr>
              ))
            )}
          </tbody>
        </table>

        {/* Pagination UI */}
        {totalPages > 1 && (
          <div className={styles.pagination}>
            <Link
              href={`/customers?page=${currentPage - 1}`}
              className={`${styles.pageBtn} ${currentPage <= 1 ? styles.disabled : ''}`}
            >
              Previous
            </Link>

            <span className={styles.pageInfo}>
              Page {currentPage} of {totalPages}
            </span>

            <Link
              href={`/customers?page=${currentPage + 1}`}
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
