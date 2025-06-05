import { useAuthStore } from './stores/useAuthStore';

export const buildUrl = (parts: string[], queryParams?: Record<string, string | undefined>) => {
  const path = '/' + parts.filter(Boolean).join('/');

  if (!queryParams) {
    return path;
  }

  const validParams: Record<string, string> = Object.fromEntries(
    Object.entries(queryParams)
      .filter(([, value]) => typeof value === 'string')
      .map(([key, value]) => [key, value as string]),
  );

  if (Object.keys(validParams).length === 0) {
    return path;
  }

  const queryString = new URLSearchParams(validParams).toString();
  return `${path}?${queryString}`;
};

const authSitemap = Object.freeze({
  main: '/',
  visits: '/visits',
  visit: (id: number) => buildUrl(['visits', String(id)]),
  additionalServices: '/additional-services',
  patients: '/patients',
  patient: (id: number) => buildUrl(['patients', String(id)]),
  specialists: '/specialists',
  specialist: (id: number) => buildUrl(['specialists', String(id)]),
  prescriptions: '/prescriptions',
  referrals: '/referrals',
  admin: '/admin',
  login: '/',
  register: '/',
  forgotPassword: '/',
  settings: '/settings',
});

const defaultSitemap = Object.freeze({
  main: '/',
  visits: '/',
  visit: (_id: number) => '/',
  additionalServices: '/',
  patients: '/',
  patient: (_id: number) => '/',
  specialists: '/',
  specialist: (_id: number) => '/',
  prescriptions: '/',
  referrals: '/',
  admin: '/',
  login: '/login',
  register: '/register',
  forgotPassword: '/forgot-password',
  settings: '/',
});

export const useSitemap = () => {
  const authenticated = useAuthStore((state) => state.authenticated);
  if (authenticated) {
    return authSitemap;
  }
  return defaultSitemap;
};
