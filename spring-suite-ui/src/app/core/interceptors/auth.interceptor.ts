import { HttpInterceptorFn } from '@angular/common/http';

const PUBLIC_ENDPOINTS = ['/login', '/register'];

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const token = localStorage.getItem('token');

  // Skip adding token for public endpoints
  const url = req.url;
  const isPublicEndpoint = PUBLIC_ENDPOINTS.some((endpoint) =>
    url.endsWith(endpoint),
  );

  if (token && !isPublicEndpoint) {
    req = req.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`,
      },
    });
  }

  return next(req);
};
