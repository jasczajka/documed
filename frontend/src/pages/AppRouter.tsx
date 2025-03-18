import { Register } from 'modules/auth/Register';
import { lazy } from 'react';
import { createBrowserRouter, RouteObject, RouterProvider } from 'react-router';
import { ProtectedRoute } from 'shared/components/ProtectedRoute';
import { LoggedLayout } from '../modules/layouts/LoggedLayout';

const VisitsPage = lazy(() => import('./VisitsPage'));
const PatientsPage = lazy(() => import('./PatientsPage'));
const SpecialistsPage = lazy(() => import('./SpecialistsPage'));
const ReferralsPage = lazy(() => import('./ReferralsPage'));
const PrescriptionsPage = lazy(() => import('./PrescriptionsPage'));
const AdministrationPage = lazy(() => import('./AdministrationPage'));

// @TODO replace these with a useRoles hook that will determine whether user is backoffice / admin / patient
const isAdmin = true;
const isPatient = true;
const isAuthenticated = false;

const defaultRoutes: RouteObject[] = [
  {
    path: '/register',
    element: <Register />,
  },
];

const authRoutes: RouteObject[] = [
  {
    path: '/',
    element: <LoggedLayout />,
    children: [
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
    ],
  },
];

export const AppRouter = () => {
  const router = createBrowserRouter(isAuthenticated ? authRoutes : defaultRoutes);
  return <RouterProvider router={router} />;
};
