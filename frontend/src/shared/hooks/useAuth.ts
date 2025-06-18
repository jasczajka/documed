import {
  useConfirmRegistration,
  useDeactivateAccount,
  useGetCurrentUser,
  useLogin,
  useLogout,
  useRequestRegistration,
} from 'shared/api/generated/auth-controller/auth-controller';
import { UserRole } from 'shared/api/generated/generated.schemas';
import { mapAuthError } from 'shared/utils/mapAuthError';
import { useAllServicesStore } from './stores/useAllServicesStore';
import { useAuthStore } from './stores/useAuthStore';
import { useDoctorsStore } from './stores/useDoctorsStore';
import { useFacilityStore } from './stores/useFacilityStore';
import { useReferralTypesStore } from './stores/useReferralTypesStore';
import { useSpecializationsStore } from './stores/useSpecializationsStore';
import { useSubscriptionStore } from './stores/useSubscriptionStore';

export const useAuth = () => {
  const { user, authenticateUser, clearUser } = useAuthStore();

  const { mutateAsync: loginMutation, isPending: isLoginPending, error: loginError } = useLogin();
  const {
    mutateAsync: requestRegisterMutation,
    isPending: isRequestRegisterPending,
    error: requestRegisterError,
  } = useRequestRegistration();

  const {
    mutateAsync: confirmRegisterMutation,
    isPending: isConfirmRegisterPending,
    error: confirmRegisterError,
  } = useConfirmRegistration();
  const {
    mutateAsync: logoutMutation,
    isPending: isLogoutPending,
    error: logoutError,
  } = useLogout();
  const {
    mutateAsync: deleteAccountMutation,
    isPending: isDeleteAccountPending,
    error: deleteAccountError,
  } = useDeactivateAccount();
  const { refetch: fetchCurrentUser, isLoading: isGetCurrentUserLoading } = useGetCurrentUser({
    query: {
      enabled: false,
      retry: false,
    },
  });

  const verifyAuthentication = async (): Promise<boolean> => {
    try {
      const { data } = await fetchCurrentUser();
      if (data) {
        authenticateUser(data);
        return true;
      } else {
        clearUser();
        return false;
      }
    } catch (error) {
      console.error('Auth check failed:', error);
      clearUser();
      return false;
    }
  };

  const login = async (credentials: { login: string; password: string; facilityId: number }) => {
    try {
      await loginMutation({ data: credentials });
      const { data } = await fetchCurrentUser();
      if (data) {
        authenticateUser(data);
        const [subscriptions, doctors, services, specializations, referralTypes] =
          await Promise.allSettled([
            useFacilityStore.getState().fetchFacilities(),
            useSubscriptionStore.getState().fetchSubscriptions(),
            useDoctorsStore.getState().fetchDoctors(),
            useAllServicesStore.getState().fetchAllServices(),
            useSpecializationsStore.getState().fetchSpecializations(),
            useReferralTypesStore.getState().fetchReferralTypes(),
          ]);

        [subscriptions, doctors, services, specializations, referralTypes].forEach((result) => {
          if (result.status === 'rejected') {
            console.error('Data fetch failed:', result.reason);
          }
        });
      }
    } catch (error) {
      console.error('Error logging in: ', error);
      throw error;
    }
  };

  const requestRegister = async (userData: {
    firstName: string;
    lastName: string;
    email: string;
    password: string;
    confirmPassword: string;
    address: string;
    phoneNumber: string;
    pesel?: string;
    birthdate: string;
  }) => {
    const response = await requestRegisterMutation({ data: userData });
    return response;
  };

  const confirmRegistration = async (userData: { email: string; otp: string }) => {
    const response = await confirmRegisterMutation({ data: userData });
    return response;
  };

  const logout = async () => {
    await logoutMutation();
    clearUser();
  };

  const deleteAccount = async (userId: number) => {
    try {
      await deleteAccountMutation({ id: userId });
    } finally {
      clearUser();
    }
  };

  const isAdmin = user?.role === UserRole.ADMINISTRATOR;
  const isPatient = user?.role === UserRole.PATIENT;
  const isDoctor = user?.role === UserRole.DOCTOR;
  const isWardClerk = user?.role === UserRole.WARD_CLERK;
  const isNurse = user?.role === UserRole.NURSE;
  const isStaff = user?.role !== UserRole.PATIENT;
  const hasRole = (role: UserRole) => user?.role === role;
  const hasAnyRole = (roles: UserRole[]) => !!user?.role && roles.includes(user.role);

  return {
    loading:
      isLoginPending ||
      isRequestRegisterPending ||
      isConfirmRegisterPending ||
      isLogoutPending ||
      isDeleteAccountPending ||
      isGetCurrentUserLoading,
    user,
    login,
    logout,
    requestRegister,
    confirmRegistration,
    deleteAccount,
    verifyAuthentication,
    isAdmin,
    isPatient,
    isWardClerk,
    isNurse,
    isDoctor,
    isStaff,
    hasRole,
    hasAnyRole,
    loginError: mapAuthError(loginError),
    logoutError: mapAuthError(logoutError),
    requestRegisterError: mapAuthError(requestRegisterError),
    confirmRegisterError: mapAuthError(confirmRegisterError),
    deleteAccountError,
  };
};
