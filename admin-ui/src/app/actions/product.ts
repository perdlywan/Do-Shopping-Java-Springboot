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

export async function addProductAction(prevState: any, formData: FormData) {
  const name = formData.get('name');
  const categoryId = formData.get('categoryId');
  const price = formData.get('price');
  const stock = formData.get('stock');
  const description = formData.get('description');

  if (!name || !categoryId || !price || !stock) {
    return { error: 'All required fields must be filled.' };
  }

  try {
    const headers = await getAuthHeaders();
    const res = await fetch('http://localhost:8080/products', {
      method: 'POST',
      headers,
      body: JSON.stringify({
        name,
        categoryId,
        price: Number(price),
        stock: Number(stock),
        description
      }),
    });

    if (!res.ok) {
      const data = await res.json().catch(() => ({}));
      return { error: data.message || `Failed to add product (Status: ${res.status})` };
    }
  } catch (error) {
    return { error: 'Failed to connect to server.' };
  }

  revalidatePath('/products');
  redirect('/products?success=Product added successfully');
}

export async function editProductAction(id: string, prevState: any, formData: FormData) {
  const name = formData.get('name');
  const categoryId = formData.get('categoryId');
  const price = formData.get('price');
  const stock = formData.get('stock');
  const description = formData.get('description');

  if (!name || !categoryId || !price || !stock) {
    return { error: 'All required fields must be filled.' };
  }

  try {
    const headers = await getAuthHeaders();
    const res = await fetch(`http://localhost:8080/products/${id}`, {
      method: 'PUT',
      headers,
      body: JSON.stringify({
        name,
        categoryId,
        price: Number(price),
        stock: Number(stock),
        description
      }),
    });

    if (!res.ok) {
      const data = await res.json().catch(() => ({}));
      return { error: data.message || `Failed to update product (Status: ${res.status})` };
    }
  } catch (error) {
    return { error: 'Failed to connect to server.' };
  }

  revalidatePath('/products');
  redirect('/products?success=Product updated successfully');
}

export async function deleteProductAction(id: string) {
  try {
    const headers = await getAuthHeaders();
    const res = await fetch(`http://localhost:8080/products/${id}`, {
      method: 'DELETE',
      headers,
    });

    if (!res.ok) {
      throw new Error(`Failed to delete product (Status: ${res.status})`);
    }

    revalidatePath('/products');
  } catch (error) {
    console.error('Delete product error:', error);
    throw new Error('Failed to connect to server while deleting product.');
  }
}
