'use server';

import { cookies } from 'next/headers';
import { redirect } from 'next/navigation';

export async function loginAction(prevState: any, formData: FormData) {
  const username = formData.get('username');
  const password = formData.get('password');

  if (!username || !password) {
    return { error: 'Username and Password are required.' };
  }

  try {
    const res = await fetch('http://localhost:8080/auth/login', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({ username, password }),
    });

    if (!res.ok) {
      if (res.status === 401 || res.status === 403) {
        return { error: 'Incorrect username or password.' };
      }
      return { error: `Login failed: ${res.statusText}` };
    }

    const data = await res.json();

    if (data.role !== 'ADMIN') {
      return { error: 'Incorrect username or password.' };
    }
    
    // Asumsikan struktur response: { token: '...', type: 'Bearer', role: '...' }
    if (data.token) {
      const cookieStore = await cookies();
      cookieStore.set('token', data.token, {
        httpOnly: true,
        secure: process.env.NODE_ENV === 'production',
        sameSite: 'lax',
        maxAge: 60 * 60 * 24, // 1 day
        path: '/',
      });
    } else {
      return { error: 'Invalid token format from server.' };
    }
  } catch (error) {
    return { error: 'Failed to connect to backend server.' };
  }

  // Redirect ke halaman utama jika berhasil
  redirect('/');
}

export async function logoutAction() {
  const cookieStore = await cookies();
  cookieStore.delete('token');
  redirect('/login');
}
