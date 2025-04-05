import { lazy } from 'react';
import { createBrowserRouter, Navigate, RouteObject, RouterProvider } from 'react-router';
import { FileUpload } from 'shared/components/FileUpload/FileUpload';
import { ProtectedRoute } from 'shared/components/ProtectedRoute';
import { useAuthStore } from 'shared/hooks/stores/useAuthStore';
import { LoggedLayout } from '../modules/layouts/LoggedLayout';

const LoginPage = lazy(() => import('../modules/auth/LoginPage'));
const RegisterPage = lazy(() => import('../modules/auth/RegisterPage'));
const VisitsPage = lazy(() => import('./VisitsPage'));
const PatientsPage = lazy(() => import('./PatientsPage'));
const SpecialistsPage = lazy(() => import('./SpecialistsPage'));
const ReferralsPage = lazy(() => import('./ReferralsPage'));
const PrescriptionsPage = lazy(() => import('./PrescriptionsPage'));
const AdministrationPage = lazy(() => import('./AdministrationPage'));
const TestBlankPage = lazy(() => import('./TestBlankPage'));

// @TODO replace these with a useRoles hook that will determine whether user is backoffice / admin / patient
const isAdmin = true;
const isPatient = true;

const defaultRoutes: RouteObject[] = [
  // test path for testing purposes only @TODO to remove in the future
  {
    path: '/test',
    element: (
      <TestBlankPage>
        <FileUpload />
      </TestBlankPage>
    ),
  },
  {
    path: '/',
    element: <Navigate to="/register" replace />,
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
  const { user } = useAuthStore();
  const isAuthenticated = user !== null;
  const router = createBrowserRouter(isAuthenticated ? authRoutes : defaultRoutes);
  return <RouterProvider router={router} />;
};
