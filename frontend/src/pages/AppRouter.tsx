import { LoggedLayout } from 'modules/layouts/LoggedLayout';
import { SettingsTabs } from 'modules/settings/SettingsTabs';
import { lazy, useLayoutEffect, useMemo, useState } from 'react';
import { createBrowserRouter, Navigate, RouteObject, RouterProvider } from 'react-router';
import { FullPageLoadingSpinner } from 'shared/components/FullPageLoadingSpinner';
import { ProtectedRoute } from 'shared/components/ProtectedRoute';
import { useAllServicesStore } from 'shared/hooks/stores/useAllServicesStore';
import { useAuthStore } from 'shared/hooks/stores/useAuthStore';
import { useDoctorsStore } from 'shared/hooks/stores/useDoctorsStore';
import { useFacilityStore } from 'shared/hooks/stores/useFacilityStore';
import { useReferralTypesStore } from 'shared/hooks/stores/useReferralTypesStore';
import { useSpecializationsStore } from 'shared/hooks/stores/useSpecializationsStore';
import { useSubscriptionStore } from 'shared/hooks/stores/useSubscriptionStore';
import { useAuth } from 'shared/hooks/useAuth';

const LoginPage = lazy(() => import('../modules/auth/LoginPage'));
const RegisterPage = lazy(() => import('../modules/auth/RegisterPage'));
const ForgotPasswordPage = lazy(() => import('../modules/auth/ForgotPasswordPage'));
const VisitsPage = lazy(() => import('./VisitsPage'));
const AdditionalServicesPage = lazy(() => import('./AdditionalServicesPage'));
const PatientsPage = lazy(() => import('./PatientsPage'));
const SpecialistsPage = lazy(() => import('./SpecialistsPage'));
const ReferralsPage = lazy(() => import('./ReferralsPage'));
const PrescriptionsPage = lazy(() => import('./PrescriptionsPage'));
const AdministrationPage = lazy(() => import('./AdministrationPage'));
const SingleSpecialistPage = lazy(() => import('./SingleSpecialistPage'));
const SinglePatientPage = lazy(() => import('./SinglePatientPage'));
const SingleVisitPage = lazy(() => import('./SingleVisitPage'));

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

const getAuthRoutes = (
  isAdmin: boolean,
  isPatient: boolean,
  canEditDoctorData: boolean,
  canSeePrescriptions: boolean,
  isStaff: boolean,
): RouteObject[] => [
  {
    path: '/',
    element: <LoggedLayout />,
    children: [
      { path: '/', element: <Navigate to="/visits" replace /> },
      { path: '/visits', element: <VisitsPage /> },
      { path: '/visits/:id', element: <SingleVisitPage /> },
      { path: '/additional-services', element: <AdditionalServicesPage /> },
      {
        path: '/patients',
        element: (
          <ProtectedRoute element={<PatientsPage />} isAllowed={!isPatient} redirectTo="/visits" />
        ),
      },
      { path: '/specialists', element: <SpecialistsPage /> },
      { path: '/referrals', element: <ReferralsPage /> },
      {
        path: '/prescriptions',
        element: (
          <ProtectedRoute
            element={<PrescriptionsPage />}
            isAllowed={canSeePrescriptions}
            redirectTo="/visits"
          />
        ),
      },
      { path: '/settings', element: <SettingsTabs /> },
      {
        path: '/specialists/:id',
        element: (
          <ProtectedRoute
            element={<SingleSpecialistPage />}
            isAllowed={canEditDoctorData}
            redirectTo="/visits"
          />
        ),
      },
      {
        path: '/patients/:id',
        element: (
          <ProtectedRoute
            element={<SinglePatientPage />}
            isAllowed={isStaff}
            redirectTo="/visits"
          />
        ),
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
      { path: '*', element: <Navigate to="/visits" replace /> },
    ],
  },
];

export const AppRouter = () => {
  const {
    verifyAuthentication,
    loading,
    isAdmin,
    isPatient,
    canEditDoctorData,
    canSeePrescriptions,
    isStaff,
  } = useAuth();
  const authenticated = useAuthStore((state) => state.authenticated);
  const fetchFacilities = useFacilityStore((state) => state.fetchFacilities);
  const fetchSubscriptions = useSubscriptionStore((state) => state.fetchSubscriptions);
  const fetchDoctors = useDoctorsStore((state) => state.fetchDoctors);
  const fetchAllServices = useAllServicesStore((state) => state.fetchAllServices);
  const fetchSpecializations = useSpecializationsStore((state) => state.fetchSpecializations);
  const fetchReferralTypes = useReferralTypesStore((state) => state.fetchReferralTypes);
  const [authChecked, setAuthChecked] = useState(false);
  const router = useMemo(() => {
    const routes = authenticated
      ? getAuthRoutes(isAdmin, isPatient, canEditDoctorData, canSeePrescriptions, isStaff)
      : getDefaultRoutes();
    return createBrowserRouter(routes);
  }, [authenticated, isPatient, isAdmin]);

  useLayoutEffect(() => {
    const init = async () => {
      try {
        await Promise.all([
          fetchFacilities(),
          fetchSubscriptions(),
          fetchDoctors(),
          fetchAllServices(),
          fetchSpecializations(),
          fetchReferralTypes(),
          verifyAuthentication(),
        ]);
        setAuthChecked(true);
      } catch (error) {
        console.error(error);
      }
    };

    init();
  }, []);

  if (loading || !authChecked) {
    return <FullPageLoadingSpinner />;
  }

  return <RouterProvider router={router} key={authenticated ? 'auth' : 'default'} />;
};
