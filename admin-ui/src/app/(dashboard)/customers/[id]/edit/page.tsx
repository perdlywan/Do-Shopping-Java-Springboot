import { cookies } from 'next/headers';
import { notFound } from 'next/navigation';
import EditCustomerForm from './EditCustomerForm';
import styles from '../../../products/page.module.css';

interface CustomerDetail {
  id: string;
  name: string;
  phone: string;
}

async function getCustomerDetail(id: string) {
  try {
    const cookieStore = await cookies();
    const token = cookieStore.get('token')?.value;

    const headers: HeadersInit = {};
    if (token) {
      headers['Authorization'] = `Bearer ${token}`;
    }

    const res = await fetch(`http://localhost:8080/customers/${id}`, {
      cache: 'no-store',
      headers
    });

    if (!res.ok) {
      if (res.status === 404) return null;
      throw new Error(`Failed to fetch customer: ${res.status}`);
    }

    const json = await res.json();
    return json.data as CustomerDetail;
  } catch (error) {
    console.error(error);
    return null;
  }
}

export default async function EditCustomerPage({
  params,
}: {
  params: Promise<{ id: string }>;
}) {
  const resolvedParams = await params;
  const customer = await getCustomerDetail(resolvedParams.id);

  if (!customer) {
    notFound();
  }

  return (
    <div className={styles.container}>
      <div className={styles.header}>
        <h1 className={styles.title}>Edit Customer Profile</h1>
      </div>

      <div className={styles.card}>
        <EditCustomerForm
          id={customer.id}
          name={customer.name}
          phone={customer.phone}
        />
      </div>
    </div>
  );
}
