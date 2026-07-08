import { cookies } from 'next/headers';
import styles from './page.module.css';
import ReportsFilter from './ReportsFilter';
import ExportButton from './ExportButton';

const API_BASE = 'http://localhost:8080/reports';

async function fetchReport(endpoint: string, searchParams: { [key: string]: string | undefined }) {
  const cookieStore = await cookies();
  const token = cookieStore.get('token')?.value;

  const params = new URLSearchParams();
  if (searchParams.startDate) params.append('startDate', searchParams.startDate);
  if (searchParams.endDate) params.append('endDate', searchParams.endDate);
  
  const queryString = params.toString() ? `?${params.toString()}` : '';

  try {
    const res = await fetch(`${API_BASE}/${endpoint}${queryString}`, {
      headers: {
        'Authorization': `Bearer ${token}`
      },
      cache: 'no-store'
    });
    
    if (!res.ok) {
      console.error(`Failed to fetch ${endpoint}:`, await res.text());
      return { data: [] };
    }
    
    return res.json();
  } catch (error) {
    console.error(`Error fetching ${endpoint}:`, error);
    return { data: [] };
  }
}

export default async function ReportsPage({
  searchParams,
}: {
  searchParams: Promise<{ [key: string]: string | undefined }>;
}) {
  const resolvedParams = await searchParams;
  
  const [salesRes, topProductsRes, topCustomersRes] = await Promise.all([
    fetchReport('sales', resolvedParams),
    fetchReport('top-products', resolvedParams),
    fetchReport('top-customers', resolvedParams)
  ]);

  const salesData = salesRes?.data || [];
  const topProductsData = topProductsRes?.data || [];
  const topCustomersData = topCustomersRes?.data || [];

  // Reconstruct query string for export links
  const queryParams = new URLSearchParams();
  if (resolvedParams.startDate) queryParams.append('startDate', resolvedParams.startDate);
  if (resolvedParams.endDate) queryParams.append('endDate', resolvedParams.endDate);
  const qString = queryParams.toString() ? `?${queryParams.toString()}` : '';

  return (
    <div className={styles.container}>
      <div className={styles.header}>
        <h1 className={styles.title}>Reports & Analytics</h1>
      </div>

      <ReportsFilter />

      <div className={styles.grid}>
        {/* Sales Report Card */}
        <div className={styles.card}>
          <div className={styles.cardHeader}>
            <h2 className={styles.cardTitle}>Sales Report</h2>
            <ExportButton endpoint="sales" queryString={qString} fileName="laporan_penjualan.xlsx" />
          </div>
          <div style={{ overflowX: 'auto' }}>
            <table className={styles.table}>
              <thead>
                <tr>
                  <th>Date</th>
                  <th>Orders</th>
                  <th>Revenue (Rp)</th>
                </tr>
              </thead>
              <tbody>
                {salesData.length > 0 ? (
                  salesData.map((item: any, idx: number) => (
                    <tr key={idx}>
                      <td>{item.tanggal}</td>
                      <td>{item.totalOrder}</td>
                      <td>{item.totalPenjualan.toLocaleString('id-ID')}</td>
                    </tr>
                  ))
                ) : (
                  <tr>
                    <td colSpan={3} className={styles.emptyState}>No sales data found for this period.</td>
                  </tr>
                )}
              </tbody>
            </table>
          </div>
        </div>

        {/* Top Products Card */}
        <div className={styles.card}>
          <div className={styles.cardHeader}>
            <h2 className={styles.cardTitle}>Top Products</h2>
            <ExportButton endpoint="top-products" queryString={qString} fileName="top_produk.xlsx" />
          </div>
          <div style={{ overflowX: 'auto' }}>
            <table className={styles.table}>
              <thead>
                <tr>
                  <th>Product Name</th>
                  <th>Units Sold</th>
                </tr>
              </thead>
              <tbody>
                {topProductsData.length > 0 ? (
                  topProductsData.map((item: any, idx: number) => (
                    <tr key={idx}>
                      <td>{item.nama}</td>
                      <td>{item.totalTerjual}</td>
                    </tr>
                  ))
                ) : (
                  <tr>
                    <td colSpan={2} className={styles.emptyState}>No product sales data found.</td>
                  </tr>
                )}
              </tbody>
            </table>
          </div>
        </div>

        {/* Top Customers Card */}
        <div className={styles.card}>
          <div className={styles.cardHeader}>
            <h2 className={styles.cardTitle}>Top Customers</h2>
            <ExportButton endpoint="top-customers" queryString={qString} fileName="top_customer.xlsx" />
          </div>
          <div style={{ overflowX: 'auto' }}>
            <table className={styles.table}>
              <thead>
                <tr>
                  <th>Customer Name</th>
                  <th>Total Orders</th>
                </tr>
              </thead>
              <tbody>
                {topCustomersData.length > 0 ? (
                  topCustomersData.map((item: any, idx: number) => (
                    <tr key={idx}>
                      <td>{item.nama}</td>
                      <td>{item.totalOrder}</td>
                    </tr>
                  ))
                ) : (
                  <tr>
                    <td colSpan={2} className={styles.emptyState}>No customer data found.</td>
                  </tr>
                )}
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </div>
  );
}
