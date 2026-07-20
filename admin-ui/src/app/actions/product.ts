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
  const file = formData.get('file') as File | null;

  if (!name || !categoryId || !price || !stock) {
    return { error: 'All required fields must be filled.' };
  }

  try {
    const cookieStore = await cookies();
    const token = cookieStore.get('token')?.value;
    const authHeaders = {
      'Authorization': `Bearer ${token}`
    };

    const uploadForm = new FormData();
    const dataObj = {
      name,
      categoryId,
      price: Number(price),
      stock: Number(stock),
      description
    };
    
    uploadForm.append('data', new Blob([JSON.stringify(dataObj)], { type: 'application/json' }));

    if (file && file.size > 0) {
      uploadForm.append('file', file);
    }

    const res = await fetch('http://localhost:8080/products', {
      method: 'POST',
      headers: authHeaders,
      body: uploadForm,
    });

    if (!res.ok) {
      const data = await res.json().catch(() => ({}));
      return { error: data.message || `Failed to add product (Status: ${res.status})` };
    }
  } catch (error) {
    console.error('Failed to add product:', error);
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
  const file = formData.get('file') as File | null;

  if (!name || !categoryId || !price || !stock) {
    return { error: 'All required fields must be filled.' };
  }

  try {
    const cookieStore = await cookies();
    const token = cookieStore.get('token')?.value;
    const authHeaders = {
      'Authorization': `Bearer ${token}`
    };

    const uploadForm = new FormData();
    const dataObj = {
      name,
      categoryId,
      price: Number(price),
      stock: Number(stock),
      description
    };
    
    uploadForm.append('data', new Blob([JSON.stringify(dataObj)], { type: 'application/json' }));

    if (file && file.size > 0) {
      uploadForm.append('file', file);
    }

    const res = await fetch(`http://localhost:8080/products/${id}`, {
      method: 'PUT',
      headers: authHeaders,
      body: uploadForm,
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

export async function uploadProductImageAction(id: string, prevState: any, formData: FormData) {
  const file = formData.get('file') as File;

  if (!file || file.size === 0) {
    return { error: 'Please select an image file.' };
  }

  try {
    const cookieStore = await cookies();
    const token = cookieStore.get('token')?.value;

    const uploadForm = new FormData();
    uploadForm.append('file', file);

    const res = await fetch(`http://localhost:8080/products/${id}/image`, {
      method: 'POST',
      headers: {
        'Authorization': `Bearer ${token}`
      },
      body: uploadForm,
    });

    if (!res.ok) {
      const data = await res.json().catch(() => ({}));
      return { error: data.message || `Failed to upload image (Status: ${res.status})` };
    }

    const result = await res.json();
    revalidatePath('/products');
    revalidatePath(`/products/edit/${id}`);
    return { success: true, imageUrl: result.data?.imageUrl };
  } catch (error) {
    return { error: 'Failed to connect to server.' };
  }
}

export async function deleteProductImageAction(id: string) {
  try {
    const headers = await getAuthHeaders();
    const res = await fetch(`http://localhost:8080/products/${id}/image`, {
      method: 'DELETE',
      headers,
    });

    if (!res.ok) {
      throw new Error(`Failed to delete image (Status: ${res.status})`);
    }

    revalidatePath('/products');
    revalidatePath(`/products/edit/${id}`);
  } catch (error) {
    console.error('Delete product image error:', error);
    throw new Error('Failed to connect to server while deleting product image.');
  }
}
