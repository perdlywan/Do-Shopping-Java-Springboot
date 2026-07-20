'use server';

import { cookies } from 'next/headers';
import { revalidatePath } from 'next/cache';
import { redirect } from 'next/navigation';

async function getAuthHeaders() {
  const cookieStore = await cookies();
  const token = cookieStore.get('token')?.value;
  return {
    'Content-Type': 'application/json',
    'Authorization': `Bearer ${token}`
  };
}

export async function deactivateCustomerAction(id: string) {
  try {
    const headers = await getAuthHeaders();
    const res = await fetch(`http://localhost:8080/customers/${id}/deactivate`, {
      method: 'PATCH',
      headers,
    });

    if (!res.ok) {
      throw new Error(`Failed to deactivate customer (Status: ${res.status})`);
    }

    revalidatePath(`/customers`);
    revalidatePath(`/customers/${id}`);
  } catch (error) {
    console.error('Deactivate customer error:', error);
    throw new Error('Failed to connect to server while deactivating customer.');
  }
}

export async function activateCustomerAction(id: string) {
  try {
    const headers = await getAuthHeaders();
    const res = await fetch(`http://localhost:8080/customers/${id}/activate`, {
      method: 'PATCH',
      headers,
    });

    if (!res.ok) {
      throw new Error(`Failed to activate customer (Status: ${res.status})`);
    }

    revalidatePath(`/customers`);
    revalidatePath(`/customers/${id}`);
  } catch (error) {
    console.error('Activate customer error:', error);
    throw new Error('Failed to connect to server while activating customer.');
  }
}
