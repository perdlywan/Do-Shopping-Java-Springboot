import styles from "./page.module.css";
import { cookies } from "next/headers";


interface DashboardLowStockProduct {
  productId: string;
  productName: string;
  categoryName: string;
  stock: number;
}

interface DashboardSummary {
  totalRevenue: number;
  totalOrders: number;
  totalCustomers: number;
  totalProducts: number;
  lowStockProducts: DashboardLowStockProduct[];
  pendingOrders: number;
  outOfStockProducts: number;
  slowMovingProducts: DashboardLowStockProduct[];
}

interface DashboardResponse {
  statusCode: number;
  data: DashboardSummary;
}

async function getDashboardSummary(): Promise<DashboardResponse | null> {
  try {
    const cookieStore = await cookies();
    const token = cookieStore.get("token")?.value;

    const headers: HeadersInit = {};
    if (token) {
      headers["Authorization"] = `Bearer ${token}`;
    }

    const res = await fetch(`http://localhost:8080/dashboard/summary`, {
      cache: "no-store",
      headers,
    });

    if (!res.ok) {
      throw new Error(`Failed to fetch dashboard summary: ${res.status}`);
    }

    return (await res.json()) as DashboardResponse;
  } catch (error) {
    console.error(error);
    return null;
  }
}

export default async function Home() {
  const currentDate = new Date().toLocaleDateString("en-US", {
    weekday: "long",
    year: "numeric",
    month: "long",
    day: "numeric",
  });

  const response = await getDashboardSummary();
  const summary = response?.data;

  const formatCurrency = (amount: number) => {
    return new Intl.NumberFormat("id-ID", {
      style: "currency",
      currency: "IDR",
      minimumFractionDigits: 0,
      maximumFractionDigits: 0,
    }).format(amount);
  };

  // Formatter for Date
  const formatDate = (dateStr: string) => {
    return new Date(dateStr).toLocaleDateString("en-US", {
      month: "short",
      day: "numeric",
      year: "numeric",
    });
  };

  return (
    <div className={styles.dashboard}>
      <div className={styles.header}>
        <h1 className={styles.title}>Dashboard Overview</h1>
        <span className={styles.date}>{currentDate}</span>
      </div>

      {!summary && (
        <div style={{ color: 'red', padding: '1rem', backgroundColor: '#fee2e2', borderRadius: '8px' }}>
          Failed to fetch dashboard summary from server. Ensure the Spring Boot backend is running on port 8080.
        </div>
      )}

      <div className={styles.statsGrid}>
        <div className={styles.statCard}>
          <span className={styles.statLabel}>Total Revenue</span>
          <span className={styles.statValue}>
            {summary ? formatCurrency(summary.totalRevenue) : "Rp 0,00"}
          </span>
        </div>
        <div className={styles.statCard}>
          <span className={styles.statLabel}>Total Orders</span>
          <span className={styles.statValue}>{summary?.totalOrders || 0}</span>
        </div>
        <div className={styles.statCard}>
          <span className={styles.statLabel}>Active Customers</span>
          <span className={styles.statValue}>{summary?.totalCustomers || 0}</span>
        </div>
        <div className={styles.statCard}>
          <span className={styles.statLabel}>Total Products</span>
          <span className={styles.statValue}>{summary?.totalProducts || 0}</span>
        </div>
        <div className={styles.statCard}>
          <span className={styles.statLabel}>Pending Orders</span>
          <span className={styles.statValue} style={{ color: '#d97706' }}>{summary?.pendingOrders || 0}</span>
        </div>
        <div className={styles.statCard}>
          <span className={styles.statLabel}>Out of Stock</span>
          <span className={styles.statValue} style={{ color: '#dc2626' }}>{summary?.outOfStockProducts || 0}</span>
        </div>
      </div>

      <div className={styles.recentOrders} style={{ marginTop: '2rem' }}>
        <h2 className={styles.sectionTitle}>Low Stock Alerts</h2>
        <table className={styles.table}>
          <thead>
            <tr>
              <th>Product Name</th>
              <th>Category</th>
              <th>Current Stock</th>
              <th>Status</th>
            </tr>
          </thead>
          <tbody>
            {(!summary?.lowStockProducts || summary.lowStockProducts.length === 0) ? (
              <tr>
                <td colSpan={4} style={{ textAlign: "center", padding: "2rem", color: "#666" }}>
                  All products have sufficient stock.
                </td>
              </tr>
            ) : (
              summary.lowStockProducts.map((product) => (
                <tr key={product.productId}>
                  <td style={{ fontWeight: 500 }}>{product.productName}</td>
                  <td>{product.categoryName}</td>
                  <td style={{ color: product.stock === 0 ? '#dc2626' : '#d97706', fontWeight: 600 }}>
                    {product.stock}
                  </td>
                  <td>
                    <span
                      className={styles.status}
                      style={{
                        backgroundColor: product.stock === 0 ? '#fee2e2' : '#fef3c7',
                        color: product.stock === 0 ? '#991b1b' : '#92400e',
                        padding: '4px 8px',
                        borderRadius: '4px',
                        fontSize: '0.85rem',
                        fontWeight: 500
                      }}
                    >
                      {product.stock === 0 ? 'Out of Stock' : 'Low Stock'}
                    </span>
                  </td>
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>

      <div className={styles.recentOrders} style={{ marginTop: '2rem', marginBottom: '2rem' }}>
        <h2 className={styles.sectionTitle}>Slow Moving Products</h2>
        <table className={styles.table}>
          <thead>
            <tr>
              <th>Product Name</th>
              <th>Category</th>
              <th>Current Stock</th>
              <th>Status</th>
            </tr>
          </thead>
          <tbody>
            {(!summary?.slowMovingProducts || summary.slowMovingProducts.length === 0) ? (
              <tr>
                <td colSpan={4} style={{ textAlign: "center", padding: "2rem", color: "#666" }}>
                  No slow moving products found.
                </td>
              </tr>
            ) : (
              summary.slowMovingProducts.map((product) => (
                <tr key={product.productId}>
                  <td style={{ fontWeight: 500 }}>{product.productName}</td>
                  <td>{product.categoryName}</td>
                  <td>{product.stock}</td>
                  <td>
                    <span
                      className={styles.status}
                      style={{
                        backgroundColor: '#f3f4f6',
                        color: '#4b5563',
                        padding: '4px 8px',
                        borderRadius: '4px',
                        fontSize: '0.85rem',
                        fontWeight: 500
                      }}
                    >
                      Slow Moving
                    </span>
                  </td>
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
}
