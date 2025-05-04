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
import { useAuthStore } from './stores/useAuthStore';

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

  const verifyAuthentication = async () => {
    try {
      const { data } = await fetchCurrentUser();
      console.log('data from verify authentication');
      if (data) {
        authenticateUser(data);
      } else {
        clearUser();
      }
    } catch (error) {
      console.error('Auth check failed:', error);
    }
  };

  const login = async (credentials: { login: string; password: string }) => {
    try {
      await loginMutation({ data: credentials });
      const { data } = await fetchCurrentUser();
      if (data) {
        authenticateUser(data);
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
      await verifyAuthentication();
    } finally {
      clearUser();
    }
  };

  const isAdmin = user?.role === UserRole.ADMINISTRATOR;
  const isPatient = user?.role === UserRole.PATIENT;
  const isDoctor = user?.role === UserRole.DOCTOR;
  const isWardClerk = user?.role === UserRole.WARD_CLERK;
  const canEditDoctorData = isAdmin || isWardClerk;
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
    isDoctor,
    canEditDoctorData,
    hasRole,
    hasAnyRole,
    loginError: mapAuthError(loginError),
    logoutError: mapAuthError(logoutError),
    requestRegisterError: mapAuthError(requestRegisterError),
    confirmRegisterError: mapAuthError(confirmRegisterError),
    deleteAccountError,
  };
};
