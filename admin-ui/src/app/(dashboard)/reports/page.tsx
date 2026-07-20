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

import PaginatedTable from './PaginatedTable';

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
          <PaginatedTable 
            itemsPerPage={10} 
            emptyMessage="No sales data found for this period."
            headers={['Date', 'Orders', 'Revenue (Rp)']}
            rows={salesData.map((item: any) => [
              item.tanggal, 
              item.totalOrder, 
              (item.totalPenjualan || 0).toLocaleString('id-ID')
            ])}
          />
        </div>

        {/* Top Products Card */}
        <div className={styles.card}>
          <div className={styles.cardHeader}>
            <h2 className={styles.cardTitle}>Top Products</h2>
            <ExportButton endpoint="top-products" queryString={qString} fileName="top_produk.xlsx" />
          </div>
          <PaginatedTable 
            itemsPerPage={10} 
            emptyMessage="No product sales data found."
            headers={['Product Name', 'Units Sold']}
            rows={topProductsData.map((item: any) => [
              item.nama,
              item.totalTerjual
            ])}
          />
        </div>

        {/* Top Customers Card */}
        <div className={styles.card}>
          <div className={styles.cardHeader}>
            <h2 className={styles.cardTitle}>Top Customers</h2>
            <ExportButton endpoint="top-customers" queryString={qString} fileName="top_customer.xlsx" />
          </div>
          <PaginatedTable 
            itemsPerPage={10} 
            emptyMessage="No customer data found."
            headers={['Customer Name', 'Total Orders']}
            rows={topCustomersData.map((item: any) => [
              item.nama,
              item.totalOrder
            ])}
          />
        </div>
      </div>
    </div>
  );
}
