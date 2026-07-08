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

export async function addCategoryAction(prevState: any, formData: FormData) {
  const name = formData.get('name');

  if (!name) {
    return { error: 'Category Name is required.' };
  }

  try {
    const headers = await getAuthHeaders();
    const res = await fetch('http://localhost:8080/categories', {
      method: 'POST',
      headers,
      body: JSON.stringify({ name }),
    });

    if (!res.ok) {
      const data = await res.json().catch(() => ({}));
      return { error: data.message || `Failed to add category (Status: ${res.status})` };
    }
  } catch (error) {
    return { error: 'Failed to connect to server.' };
  }

  revalidatePath('/categories');
  redirect('/categories?success=Category added successfully');
}

export async function editCategoryAction(id: string, prevState: any, formData: FormData) {
  const name = formData.get('name');

  if (!name) {
    return { error: 'Category Name is required.' };
  }

  try {
    const headers = await getAuthHeaders();
    const res = await fetch(`http://localhost:8080/categories/${id}`, {
      method: 'PUT',
      headers,
      body: JSON.stringify({ name }),
    });

    if (!res.ok) {
      const data = await res.json().catch(() => ({}));
      return { error: data.message || `Failed to update category (Status: ${res.status})` };
    }
  } catch (error) {
    return { error: 'Failed to connect to server.' };
  }

  revalidatePath('/categories');
  redirect('/categories?success=Category updated successfully');
}

export async function deleteCategoryAction(id: string) {
  try {
    const headers = await getAuthHeaders();
    const res = await fetch(`http://localhost:8080/categories/${id}`, {
      method: 'DELETE',
      headers,
    });

    if (!res.ok) {
      throw new Error(`Failed to delete category (Status: ${res.status})`);
    }

    revalidatePath('/categories');
  } catch (error) {
    console.error('Delete category error:', error);
    throw new Error('Failed to connect to server while deleting category.');
  }
}
