'use server';

import { cookies } from 'next/headers';
import { redirect } from 'next/navigation';

export async function loginAction(prevState: any, formData: FormData) {
  const username = formData.get('username')?.toString();
  const password = formData.get('password')?.toString();

  if (!username || !password) {
    return { error: 'Username and Password are required.' };
  }

  try {
    const res = await fetch('http://localhost:8080/auth/login', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ username, password })
    });

    const data = await res.json();

    if (!res.ok) {
      return { error: data.message || 'Login failed. Please check your credentials.' };
    }

    if (data.role !== 'CUSTOMER') {
      return { error: 'Login failed. Please check your credentials.' };
    }

    if (data.token) {
      const cookieStore = await cookies();
      cookieStore.set('token', data.token, {
        httpOnly: true,
        secure: process.env.NODE_ENV === 'production',
        sameSite: 'lax',
        path: '/'
      });
    }

  } catch (error) {
    return { error: 'A network error occurred.' };
  }

  // Redirect on success (must be outside try-catch)
  redirect('/');
}

export async function registerAction(prevState: any, formData: FormData) {
  const username = formData.get('username')?.toString();
  const email = formData.get('email')?.toString();
  const password = formData.get('password')?.toString();
  const name = formData.get('name')?.toString();
  const phone = formData.get('phone')?.toString();

  if (!username || !email || !password || !name || !phone) {
    return { error: 'All fields are required.' };
  }

  try {
    const res = await fetch('http://localhost:8080/auth/register', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ username, email, password, name, phone })
    });

    const data = await res.json();

    if (!res.ok) {
      return { error: data.message || 'Registration failed. Username or Email might already be in use.' };
    }

  } catch (error) {
    return { error: 'A network error occurred.' };
  }

  // Redirect on success
  redirect('/login?registered=true');
}

export async function logoutAction() {
  const cookieStore = await cookies();
  cookieStore.delete('token');
  redirect('/');
}
