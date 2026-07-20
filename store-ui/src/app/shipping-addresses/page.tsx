import { getShippingAddresses } from '@/app/actions/shipping';
import AddressList from './AddressList';
import { cookies } from 'next/headers';
import { redirect } from 'next/navigation';

import Link from 'next/link';

export default async function ShippingAddressesPage({
  searchParams,
}: {
  searchParams?: Promise<{ page?: string }>;
}) {
  const cookieStore = await cookies();
  const token = cookieStore.get('store_token')?.value;

  if (!token) {
    redirect('/login');
  }

  const resolvedParams = await searchParams;
  const currentPage = Number(resolvedParams?.page) || 1;
  const { data, totalPages, error } = await getShippingAddresses(currentPage, 10);

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
        
        {(totalPages ?? 1) > 1 && (
          <div style={{ display: 'flex', justifyContent: 'center', gap: '1rem', marginTop: '2rem', marginBottom: '2rem' }}>
            {currentPage > 1 ? (
              <Link href={`/shipping-addresses?page=${currentPage - 1}`} style={{ padding: '0.5rem 1rem', background: 'var(--color-primary)', color: 'white', borderRadius: '4px', textDecoration: 'none' }}>
                Previous
              </Link>
            ) : (
              <button disabled style={{ padding: '0.5rem 1rem', background: '#ccc', color: '#666', borderRadius: '4px', border: 'none' }}>Previous</button>
            )}
            
            <span style={{ padding: '0.5rem 1rem' }}>
              Page {currentPage} of {totalPages}
            </span>

            {currentPage < (totalPages ?? 1) ? (
              <Link href={`/shipping-addresses?page=${currentPage + 1}`} style={{ padding: '0.5rem 1rem', background: 'var(--color-primary)', color: 'white', borderRadius: '4px', textDecoration: 'none' }}>
                Next
              </Link>
            ) : (
              <button disabled style={{ padding: '0.5rem 1rem', background: '#ccc', color: '#666', borderRadius: '4px', border: 'none' }}>Next</button>
            )}
          </div>
        )}
      </main>
    </div>
  );
}
