'use server';

import { cookies } from 'next/headers';

const API_BASE_URL = 'http://localhost:8080/orders';

async function getAuthHeaders() {
  const cookieStore = await cookies();
  const token = cookieStore.get('token')?.value;
  return {
    'Content-Type': 'application/json',
    'Authorization': `Bearer ${token}`,
  };
}

export async function placeOrder(orderPayload: any) {
  try {
    const res = await fetch(API_BASE_URL, {
      method: 'POST',
      headers: await getAuthHeaders(),
      body: JSON.stringify(orderPayload)
    });
    
    const data = await res.json();
    
    if (!res.ok) {
      return { error: data.message || 'Failed to place order.' };
    }
    
    return { success: true, data: data.data };
  } catch (error: any) {
    return { error: error.message || 'Network error occurred.' };
  }
}

export async function getMyOrders(page: number = 1, size: number = 10) {
  try {
    const res = await fetch(`${API_BASE_URL}/my-orders?page=${page}&size=${size}`, {
      method: 'GET',
      headers: await getAuthHeaders(),
      cache: 'no-store'
    });
    
    const data = await res.json();
    
    if (!res.ok) {
      return { error: data.message || 'Failed to fetch orders.' };
    }
    
    return { success: true, data: data.data, meta: data };
  } catch (error: any) {
    return { error: error.message || 'Network error occurred.' };
  }
}

export async function getOrderById(id: string) {
  try {
    const res = await fetch(`${API_BASE_URL}/${id}`, {
      method: 'GET',
      headers: await getAuthHeaders(),
      cache: 'no-store'
    });
    
    const data = await res.json();
    
    if (!res.ok) {
      return { error: data.message || 'Failed to fetch order details.' };
    }
    
    return { success: true, data: data.data };
  } catch (error: any) {
    return { error: error.message || 'Network error occurred.' };
  }
}
