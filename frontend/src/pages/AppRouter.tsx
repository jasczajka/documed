import { LoggedLayout } from 'modules/layouts/LoggedLayout';
import { lazy, useLayoutEffect, useMemo, useState } from 'react';
import { createBrowserRouter, Navigate, RouteObject, RouterProvider } from 'react-router';
import { FullPageLoadingSpinner } from 'shared/components/FileUpload/FullPageLoadingSpinner';
import { ProtectedRoute } from 'shared/components/ProtectedRoute';
import { useAuthStore } from 'shared/hooks/stores/useAuthStore';
import { useAuth } from 'shared/hooks/useAuth';

const LoginPage = lazy(() => import('../modules/auth/LoginPage'));
const RegisterPage = lazy(() => import('../modules/auth/RegisterPage'));
const ForgotPasswordPage = lazy(() => import('../modules/auth/ForgotPasswordPage'));
const VisitsPage = lazy(() => import('./VisitsPage'));
const PatientsPage = lazy(() => import('./PatientsPage'));
const SpecialistsPage = lazy(() => import('./SpecialistsPage'));
const ReferralsPage = lazy(() => import('./ReferralsPage'));
const PrescriptionsPage = lazy(() => import('./PrescriptionsPage'));
const AdministrationPage = lazy(() => import('./AdministrationPage'));

const getDefaultRoutes = () => [
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
    path: '/forgot-password',
    element: <ForgotPasswordPage />,
  },
  {
    path: '*',
    element: <Navigate to="/login" replace />,
  },
];

const getAuthRoutes = (isAdmin: boolean, isPatient: boolean): RouteObject[] => [
  {
    path: '/',
    element: <LoggedLayout />,
    children: [
      { path: '/', element: <Navigate to="/visits" replace /> },
      { path: '/visits', element: <VisitsPage /> },
      {
        path: '/patients',
        element: (
          <ProtectedRoute element={<PatientsPage />} isAllowed={!isPatient} redirectTo="/visits" />
        ),
      },
      { path: '/specialists', element: <SpecialistsPage /> },
      { path: '/referrals', element: <ReferralsPage /> },
      { path: '/prescriptions', element: <PrescriptionsPage /> },
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
      { path: '*', element: <Navigate to="/visits" replace /> },
    ],
  },
];

export const AppRouter = () => {
  const { verifyAuthentication, loading, isAdmin, isPatient } = useAuth();
  const authenticated = useAuthStore((state) => state.authenticated);
  const [authChecked, setAuthChecked] = useState(false);
  const router = useMemo(() => {
    const routes = authenticated ? getAuthRoutes(isAdmin, isPatient) : getDefaultRoutes();
    return createBrowserRouter(routes);
  }, [authenticated, isPatient, isAdmin]);

  useLayoutEffect(() => {
    verifyAuthentication().finally(() => {
      setAuthChecked(true);
    });
  }, []);

  if (loading || !authChecked) {
    return <FullPageLoadingSpinner />;
  }

  return <RouterProvider router={router} key={authenticated ? 'auth' : 'default'} />;
};
