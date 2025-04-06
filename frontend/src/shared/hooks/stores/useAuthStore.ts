import { UserRole } from 'shared/api/generated/generated.schemas';
import { create } from 'zustand';

export type AuthUser = {
  id: number;
  role: UserRole;
} | null;

interface AuthStoreType {
  authenticated: boolean;
  user: AuthUser;
  authenticateUser: (user: AuthUser) => void;
  clearUser: () => void;
}

export const useAuthStore = create<AuthStoreType>()((set) => ({
  authenticated: false,
  user: null,
  authenticateUser: (user: AuthUser) => {
    console.log('Authenticating user', user);
    set(() => ({ user, authenticated: !!user }));
  },
  clearUser: () => {
    console.log('Clearing user');
    set({
      user: null,
      authenticated: false,
    });
  },
}));
