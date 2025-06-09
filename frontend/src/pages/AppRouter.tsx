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

type AuthRouteOptions = {
  isAdmin: boolean;
  isDoctor: boolean;
  isPatient: boolean;
  isWardClerk: boolean;
  isNurse: boolean;
  isStaff: boolean;
};

const getAuthRoutes = ({
  isAdmin,
  isDoctor,
  isPatient,
  isWardClerk,
  isNurse,
}: AuthRouteOptions): RouteObject[] => [
  {
    path: '/',
    element: <LoggedLayout />,
    children: [
      { path: '/', element: <Navigate to="/visits" replace /> },
      { path: '/visits', element: <VisitsPage /> },
      {
        path: '/visits/:id',
        element: (
          <ProtectedRoute
            element={<SingleVisitPage />}
            isAllowed={isPatient || isDoctor}
            redirectTo="/visits"
          />
        ),
      },
      {
        path: '/additional-services',
        element: (
          <ProtectedRoute
            element={<AdditionalServicesPage />}
            isAllowed={!isAdmin}
            redirectTo="/visits"
          />
        ),
      },
      {
        path: '/patients',
        element: (
          <ProtectedRoute
            element={<PatientsPage />}
            isAllowed={isWardClerk || isDoctor || isNurse}
            redirectTo="/visits"
          />
        ),
      },
      { path: '/specialists', element: <SpecialistsPage /> },
      {
        path: '/referrals',
        element: (
          <ProtectedRoute
            element={<ReferralsPage />}
            isAllowed={isPatient}
            redirectTo="/referrals"
          />
        ),
      },
      {
        path: '/prescriptions',
        element: (
          <ProtectedRoute
            element={<PrescriptionsPage />}
            isAllowed={isPatient}
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
            isAllowed={isAdmin || isWardClerk || isDoctor}
            redirectTo="/visits"
          />
        ),
      },
      {
        path: '/patients/:id',
        element: (
          <ProtectedRoute
            element={<SinglePatientPage />}
            isAllowed={isWardClerk || isDoctor || isNurse}
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
    isDoctor,
    isWardClerk,
    isNurse,
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
      ? getAuthRoutes({
          isAdmin,
          isDoctor,
          isPatient,
          isWardClerk,
          isNurse,
          isStaff,
        })
      : getDefaultRoutes();
    return createBrowserRouter(routes);
  }, [authenticated, isPatient, isAdmin]);

  useLayoutEffect(() => {
    const init = async () => {
      try {
        await fetchFacilities();
        await verifyAuthentication();
      } catch (error) {
        console.error(error);
      } finally {
        setAuthChecked(true);
      }
    };

    init();
  }, []);

  useLayoutEffect(() => {
    if (!authenticated) {
      return;
    }

    const fetchAfterAuth = async () => {
      try {
        await Promise.allSettled([
          fetchSubscriptions(),
          fetchDoctors(),
          fetchAllServices(),
          fetchSpecializations(),
          fetchReferralTypes(),
        ]);
      } catch (err) {
        console.error(err);
      }
    };

    fetchAfterAuth();
  }, [authenticated]);

  if (loading || !authChecked) {
    return <FullPageLoadingSpinner />;
  }

  return <RouterProvider router={router} key={authenticated ? 'auth' : 'default'} />;
};
