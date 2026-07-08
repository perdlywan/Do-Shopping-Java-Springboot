'use server';

import { cookies } from 'next/headers';

const API_BASE_URL = 'http://localhost:8080/payments';

async function getAuthHeaders() {
  const cookieStore = await cookies();
  const token = cookieStore.get('token')?.value;
  return {
    'Content-Type': 'application/json',
    'Authorization': `Bearer ${token}`,
  };
}

export async function doPayment(orderId: string) {
  try {
    const res = await fetch(`${API_BASE_URL}/${orderId}`, {
      method: 'PUT',
      headers: await getAuthHeaders(),
    });

    const data = await res.json();

    if (!res.ok) {
      return { error: data.message || 'Payment failed.' };
    }

    return { success: true, data: data.data };
  } catch (error: any) {
    return { error: error.message || 'Network error occurred.' };
  }
}
