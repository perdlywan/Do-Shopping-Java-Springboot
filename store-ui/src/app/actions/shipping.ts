'use server';

import { cookies } from 'next/headers';
import { revalidatePath } from 'next/cache';

const API_BASE_URL = 'http://localhost:8080/shippingaddresses';

async function getAuthHeaders() {
  const cookieStore = await cookies();
  const token = cookieStore.get('store_token')?.value;
  return {
    'Content-Type': 'application/json',
    'Authorization': `Bearer ${token}`,
  };
}

export async function getShippingAddresses(page: number = 1, size: number = 10) {
  try {
    const res = await fetch(`${API_BASE_URL}?page=${page}&size=${size}`, {
      headers: await getAuthHeaders(),
      cache: 'no-store'
    });
    const data = await res.json();
    if (!res.ok) throw new Error(data.message || 'Failed to fetch addresses');
    return { data: data.data || [], totalPages: data.totalPages || 1 }; 
  } catch (error: any) {
    return { error: error.message || 'Network error' };
  }
}

export async function addShippingAddress(formData: FormData) {
  const address = formData.get('address')?.toString();
  const city = formData.get('city')?.toString();
  const state = formData.get('state')?.toString();
  const country = formData.get('country')?.toString();
  const postalCode = formData.get('postalCode')?.toString();

  if (!address || !city || !state || !country || !postalCode) {
    return { error: 'All fields are required.' };
  }

  try {
    const res = await fetch(API_BASE_URL, {
      method: 'POST',
      headers: await getAuthHeaders(),
      body: JSON.stringify({ address, city, state, country, postalCode })
    });
    const data = await res.json();
    
    if (!res.ok) return { error: data.message || 'Failed to add address' };
    
    revalidatePath('/shipping-addresses');
    return { success: true };
  } catch (error: any) {
    return { error: error.message || 'Network error' };
  }
}

export async function updateShippingAddress(id: string, formData: FormData) {
  const address = formData.get('address')?.toString();
  const city = formData.get('city')?.toString();
  const state = formData.get('state')?.toString();
  const country = formData.get('country')?.toString();
  const postalCode = formData.get('postalCode')?.toString();

  if (!address || !city || !state || !country || !postalCode) {
    return { error: 'All fields are required.' };
  }

  try {
    const res = await fetch(`${API_BASE_URL}/${id}`, {
      method: 'PUT',
      headers: await getAuthHeaders(),
      body: JSON.stringify({ address, city, state, country, postalCode })
    });
    const data = await res.json();
    
    if (!res.ok) return { error: data.message || 'Failed to update address' };
    
    revalidatePath('/shipping-addresses');
    return { success: true };
  } catch (error: any) {
    return { error: error.message || 'Network error' };
  }
}

export async function deleteShippingAddress(id: string) {
  try {
    const res = await fetch(`${API_BASE_URL}/${id}`, {
      method: 'DELETE',
      headers: await getAuthHeaders(),
    });
    const data = await res.json();
    
    if (!res.ok) return { error: data.message || 'Failed to delete address' };
    
    revalidatePath('/shipping-addresses');
    return { success: true };
  } catch (error: any) {
    return { error: error.message || 'Network error' };
  }
}
