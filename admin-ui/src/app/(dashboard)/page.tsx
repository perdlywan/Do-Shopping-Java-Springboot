import styles from "./page.module.css";
import { cookies } from "next/headers";

interface DashboardRecentOrder {
  orderId: string;
  orderNumber: string;
  customerName: string;
  orderDate: string;
  totalAmount: number;
  status: string;
}

interface DashboardSummary {
  totalRevenue: number;
  totalOrders: number;
  totalCustomers: number;
  totalProducts: number;
  recentOrders: DashboardRecentOrder[];
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
      </div>

      <div className={styles.recentOrders}>
        <h2 className={styles.sectionTitle}>Recent Orders</h2>
        <table className={styles.table}>
          <thead>
            <tr>
              <th>Order ID</th>
              <th>Customer</th>
              <th>Date</th>
              <th>Amount</th>
              <th>Status</th>
            </tr>
          </thead>
          <tbody>
            {(!summary?.recentOrders || summary.recentOrders.length === 0) ? (
              <tr>
                <td colSpan={5} style={{ textAlign: "center", padding: "2rem" }}>
                  No recent orders available.
                </td>
              </tr>
            ) : (
              summary.recentOrders.map((order) => (
                <tr key={order.orderId}>
                  <td>{order.orderNumber}</td>
                  <td>{order.customerName}</td>
                  <td>{formatDate(order.orderDate)}</td>
                  <td>{formatCurrency(order.totalAmount)}</td>
                  <td>
                    <span
                      className={`${styles.status} ${
                        order.status === "COMPLETED"
                          ? styles.completed
                          : styles.pending
                      }`}
                    >
                      {order.status}
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
