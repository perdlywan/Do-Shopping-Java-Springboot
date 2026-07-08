import { NextResponse } from 'next/server';
import type { NextRequest } from 'next/server';

function isTokenExpired(token: string) {
  try {
    const payloadBase64 = token.split('.')[1];
    if (!payloadBase64) return true;
    
    // atob is available in Edge Runtime
    const decodedJson = atob(payloadBase64);
    const decoded = JSON.parse(decodedJson);
    const exp = decoded.exp;
    
    if (!exp) return true;
    
    // exp is in seconds, Date.now() is in milliseconds
    return Date.now() >= exp * 1000;
  } catch (e) {
    return true; // If parsing fails, consider it expired/invalid
  }
}

export function middleware(request: NextRequest) {
  const token = request.cookies.get('token')?.value;
  const isLoginPage = request.nextUrl.pathname === '/login';

  let tokenExpired = true;
  if (token) {
    tokenExpired = isTokenExpired(token);
  }

  // If no token or token is expired, and not already on login page
  if ((!token || tokenExpired) && !isLoginPage) {
    const response = NextResponse.redirect(new URL('/login', request.url));
    if (token) {
      // Clear the invalid/expired token cookie
      response.cookies.delete('token');
    }
    return response;
  }

  // If authenticated and trying to access login page
  if (token && !tokenExpired && isLoginPage) {
    return NextResponse.redirect(new URL('/', request.url));
  }

  return NextResponse.next();
}

export const config = {
  matcher: [
    /*
     * Match all request paths except for the ones starting with:
     * - api (API routes)
     * - _next/static (static files)
     * - _next/image (image optimization files)
     * - favicon.ico, sitemap.xml, robots.txt (metadata files)
     */
    '/((?!api|_next/static|_next/image|favicon.ico|sitemap.xml|robots.txt).*)',
  ],
};
