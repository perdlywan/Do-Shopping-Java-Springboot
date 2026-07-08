import { NextRequest, NextResponse } from 'next/server';
import { cookies } from 'next/headers';

export async function GET(request: NextRequest) {
  const searchParams = request.nextUrl.searchParams;
  const endpoint = searchParams.get('endpoint');
  const qs = searchParams.get('qs') || '';

  if (!endpoint) {
    return new NextResponse('Missing endpoint', { status: 400 });
  }

  const cookieStore = await cookies();
  const token = cookieStore.get('token')?.value;

  if (!token) {
    return new NextResponse('Unauthorized', { status: 401 });
  }

  try {
    const res = await fetch(`http://localhost:8080/reports/${endpoint}/export${qs}`, {
      headers: {
        'Authorization': `Bearer ${token}`
      },
      cache: 'no-store'
    });

    if (!res.ok) {
      return new NextResponse('Failed to fetch from backend', { status: res.status });
    }

    const data = await res.arrayBuffer();
    const headers = new Headers();
    headers.set('Content-Type', res.headers.get('Content-Type') || 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet');
    headers.set('Content-Disposition', res.headers.get('Content-Disposition') || 'attachment; filename="report.xlsx"');

    return new NextResponse(data, {
      headers,
      status: 200,
    });
  } catch (error) {
    console.error('Export proxy error:', error);
    return new NextResponse('Internal Server Error', { status: 500 });
  }
}
