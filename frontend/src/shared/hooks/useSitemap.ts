// this buidler will be used later in development to use query params (e.g. patient/:id)
export const buildUrl = (parts: string[], queryParams?: Record<string, string | undefined>) => {
  const path = parts.filter(Boolean).join('/');

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
  patients: '/patients',
  specialists: '/specialists',
  prescriptions: '/prescriptions',
  referrals: '/referrals',
  admin: '/admin',
  login: '/',
  register: '/',
});

const defaultSitemap = Object.freeze({
  main: '/',
  visits: '/',
  patients: '/',
  specialists: '/',
  prescriptions: '/',
  referrals: '/',
  admin: '/',
  login: '/login',
  register: '/',
});

export const useSitemap = () => {
  const authenticated = true;
  if (authenticated) {
    return authSitemap;
  }
  return defaultSitemap;
};
