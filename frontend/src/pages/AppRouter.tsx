import { lazy, useLayoutEffect, useMemo } from 'react';
import { createBrowserRouter, Navigate, RouteObject, RouterProvider } from 'react-router';
import { FullPageLoadingSpinner } from 'shared/components/FileUpload/FullPageLoadingSpinner';
import { ProtectedRoute } from 'shared/components/ProtectedRoute';
import { useAuthStore } from 'shared/hooks/stores/useAuthStore';
import { useAuth } from 'shared/hooks/useAuth';
import { LoggedLayout } from '../modules/layouts/LoggedLayout';

const LoginPage = lazy(() => import('../modules/auth/LoginPage'));
const RegisterPage = lazy(() => import('../modules/auth/RegisterPage'));
const VisitsPage = lazy(() => import('./VisitsPage'));
const PatientsPage = lazy(() => import('./PatientsPage'));
const SpecialistsPage = lazy(() => import('./SpecialistsPage'));
const ReferralsPage = lazy(() => import('./ReferralsPage'));
const PrescriptionsPage = lazy(() => import('./PrescriptionsPage'));
const AdministrationPage = lazy(() => import('./AdministrationPage'));

const { isAdmin } = useAuth();
const { isPatient } = useAuth();

const defaultRoutes: RouteObject[] = [
  {
    path: '/',
    element: <Navigate to="/login" replace />,
  },
  {
    path: '/register',
    element: <RegisterPage />,
  },
  {
    path: '/login',
    element: <LoginPage />,
  },
  {
    path: '*',
    element: <Navigate to="/login" replace />,
  },
];

const authRoutes: RouteObject[] = [
  {
    path: '/',
    element: <LoggedLayout />,
    children: [
      {
        path: '/',
        element: <Navigate to="/visits" replace />,
      },
      {
        path: '/visits',
        element: <VisitsPage />,
      },
      {
        path: '/patients',
        element: (
          <ProtectedRoute element={<PatientsPage />} isAllowed={!isPatient} redirectTo="/visits" />
        ),
      },
      {
        path: '/specialists',
        element: <SpecialistsPage />,
      },
      {
        path: '/referrals',
        element: <ReferralsPage />,
      },
      {
        path: '/prescriptions',
        element: <PrescriptionsPage />,
      },
      {
        path: '/admin',
        element: (
          <ProtectedRoute
            element={<AdministrationPage />}
            isAllowed={isAdmin}
            redirectTo="/visits"
          />
        ),
      },
      {
        path: '*',
        element: <Navigate to="/visits" replace />,
      },
    ],
  },
];

export const AppRouter = () => {
  const { verifyAuthentication, loading } = useAuth();
  const authenticated = useAuthStore((state) => state.authenticated);
  console.log('re-rendering app router');
  console.log('authenticated? ', authenticated);
  const router = useMemo(() => {
    const routes = authenticated ? authRoutes : defaultRoutes;
    return createBrowserRouter(routes);
  }, [authenticated]);

  useLayoutEffect(() => {
    verifyAuthentication();
  }, []);

  if (loading) {
    return <FullPageLoadingSpinner />;
  }

  return <RouterProvider router={router} key={authenticated ? 'auth' : 'default'} />;
};
