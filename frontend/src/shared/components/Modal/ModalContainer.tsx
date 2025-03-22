import { useEffect } from 'react';
import { createPortal } from 'react-dom';
import { appConfig } from 'shared/appConfig';
import { useModalStore } from 'shared/hooks/stores/useModalStore';

export const ModalContainer = () => {
  const activeModals = useModalStore((state) => state.modals);
  const clearModals = useModalStore((state) => state.clearModals);
  const portalRoot = document.getElementById(appConfig.portalRootId);

  useEffect(() => {
    return clearModals;
  }, []);

  if (!activeModals.length || !portalRoot) {
    return null;
  }

  return createPortal(activeModals[0].content, portalRoot, activeModals[0].id);
};
