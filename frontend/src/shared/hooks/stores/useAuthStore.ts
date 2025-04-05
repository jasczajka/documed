import { UserRole } from 'shared/api/generated/generated.schemas';
import { create } from 'zustand';
import { persist } from 'zustand/middleware';

export type AuthUser = {
  id: number;
  role: UserRole;
} | null;

type AuthState = {
  user: AuthUser;
  setUser: (user: AuthUser) => void;
  clearUser: () => void;
};

export const useAuthStore = create<AuthState>()(
  persist(
    (set) => ({
      user: null,
      setUser: (user) => set({ user }),
      clearUser: () => set({ user: null }),
    }),
    {
      name: 'auth-storage',
    },
  ),
);
