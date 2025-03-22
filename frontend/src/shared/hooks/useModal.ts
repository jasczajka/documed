import { ReactNode, useCallback } from 'react';
import { useModalStore } from 'shared/hooks/stores/useModalStore';

type Fn = (closeModal: () => void) => ReactNode;

export const useModal = () => {
  const pushModal = useModalStore((state) => state.pushModal);
  const removeModal = useModalStore((state) => state.removeModal);

  const openModal = useCallback(
    (id: string, content: ReactNode | Fn) => {
      if (typeof content === 'function') {
        pushModal(
          id,
          content(() => removeModal(id)),
        );
        return;
      }
      pushModal(id, content);
    },
    [pushModal],
  );

  return {
    openModal,
    closeModal: removeModal,
  };
};
