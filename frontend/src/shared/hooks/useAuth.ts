import { useEffect } from 'react';
import {
  useDeleteAccount,
  useGetCurrentUser,
  useLogin,
  useLogout,
  useRegister,
} from 'shared/api/generated/auth-controller/auth-controller';
import { UserRole } from 'shared/api/generated/generated.schemas';
import { useAuthStore } from './stores/useAuthStore';
export const useAuth = () => {
  const { user, setUser, clearUser } = useAuthStore();

  const { mutateAsync: loginMutation } = useLogin();
  const { mutateAsync: registerMutation } = useRegister();
  const { mutateAsync: logoutMutation } = useLogout();
  const { mutateAsync: deleteAccountMutation } = useDeleteAccount();
  const { refetch: fetchCurrentUser } = useGetCurrentUser({
    query: {
      enabled: false,
      retry: false,
    },
  });

  useEffect(() => {
    const checkAuth = async () => {
      try {
        const { data } = await fetchCurrentUser();
        if (data && data.id && data.role) {
          setUser({ id: data.id, role: data.role });
        }
      } catch (error) {
        console.error('Auth check failed:', error);
        clearUser();
      }
    };

    if (!user) {
      checkAuth();
    }
  }, []);

  const login = async (credentials: { login: string; password: string }) => {
    try {
      await loginMutation({ data: credentials });
      const { data } = await fetchCurrentUser();
      return data;
    } catch (error) {
      console.error('Error logging in: ', error);
      clearUser();
      throw error;
    }
  };

  const register = async (userData: {
    firstName: string;
    lastName: string;
    email: string;
    password: string;
    confirmPassword: string;
    address: string;
    phoneNumber?: string;
    pesel?: string;
    birthdate: string;
  }) => {
    const response = await registerMutation({ data: userData });
    return response;
  };

  const logout = async () => {
    try {
      await logoutMutation();
    } finally {
      clearUser();
    }
  };

  const deleteAccount = async (userId: number) => {
    try {
      await deleteAccountMutation({ id: userId });
    } finally {
      clearUser();
    }
  };

  const isAdmin = () => user?.role === UserRole.ADMINISTRATOR;
  const isPatient = () => user?.role === UserRole.PATIENT;
  const isDoctor = () => user?.role === UserRole.DOCTOR;
  const isWardClerk = () => user?.role === UserRole.WARD_CLERK;
  const hasRole = (role: UserRole) => user?.role === role;
  const hasAnyRole = (roles: UserRole[]) => !!user?.role && roles.includes(user.role);

  return {
    user,
    isAuthenticated: !!user,
    login,
    logout,
    register,
    deleteAccount,
    isAdmin,
    isPatient,
    isWardClerk,
    isDoctor,
    hasRole,
    hasAnyRole,
  };
};
