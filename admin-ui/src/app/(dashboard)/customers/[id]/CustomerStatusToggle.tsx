'use client';

import { useState } from 'react';
import { useRouter } from 'next/navigation';
import { activateCustomerAction, deactivateCustomerAction } from '@/app/actions/customer';
import styles from '../products/form.module.css';

interface CustomerStatusToggleProps {
  id: string;
  status: string;
}

export default function CustomerStatusToggle({ id, status }: CustomerStatusToggleProps) {
  const [loading, setLoading] = useState(false);
  const router = useRouter();

  const handleToggle = async () => {
    const isActivating = status === 'Inactive';
    const actionName = isActivating ? 'activate' : 'deactivate';
    
    if (!confirm(`Are you sure you want to ${actionName} this customer?`)) {
      return;
    }

    setLoading(true);
    try {
      if (isActivating) {
        await activateCustomerAction(id);
      } else {
        await deactivateCustomerAction(id);
      }
      router.refresh();
    } catch (error) {
      alert(`Failed to ${actionName} customer`);
    } finally {
      setLoading(false);
    }
  };

  const isInactive = status === 'Inactive';

  return (
    <button
      onClick={handleToggle}
      disabled={loading}
      style={{
        padding: '0.5rem 1rem',
        borderRadius: '4px',
        border: 'none',
        cursor: loading ? 'not-allowed' : 'pointer',
        fontWeight: 'bold',
        backgroundColor: isInactive ? '#34a853' : '#ea4335',
        color: 'white',
        opacity: loading ? 0.7 : 1,
      }}
    >
      {loading ? 'Processing...' : isInactive ? 'Activate Account' : 'Deactivate Account'}
    </button>
  );
}
