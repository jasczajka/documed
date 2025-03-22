import { ReactNode } from 'react';
import { create } from 'zustand';

interface Modal {
  id: string;
  content: ReactNode;
}

interface ModalStoreType {
  modals: Modal[];
  pushModal: (id: string, content: ReactNode) => void;
  removeModal: (id: string) => void;
  clearModals: () => void;
}

export const useModalStore = create<ModalStoreType>((set) => ({
  modals: [],
  pushModal: (id: string, content: ReactNode) =>
    set((prev) => {
      if (prev.modals.find((modal) => modal.id === id)) {
        return prev;
      }
      return { modals: [...prev.modals, { id, content }] };
    }),
  removeModal: (id: string) =>
    set((prev) => ({ modals: prev.modals.filter((modal) => modal.id !== id) })),
  clearModals: () => set({ modals: [] }),
}));
