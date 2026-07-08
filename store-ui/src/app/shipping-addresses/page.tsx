import { getShippingAddresses } from '@/app/actions/shipping';
import AddressList from './AddressList';
import { cookies } from 'next/headers';
import { redirect } from 'next/navigation';

export default async function ShippingAddressesPage() {
  const cookieStore = await cookies();
  const token = cookieStore.get('token')?.value;

  if (!token) {
    redirect('/login');
  }

  const { data, error } = await getShippingAddresses();

  if (error) {
    return (
      <div style={{ textAlign: 'center', padding: '4rem', color: '#ef4444' }}>
        <p>Error loading addresses: {error}</p>
      </div>
    );
  }

  return (
    <div className="page-wrapper animate-fade-in">
      <main className="main-content">
        <AddressList initialAddresses={data} />
      </main>
    </div>
  );
}
