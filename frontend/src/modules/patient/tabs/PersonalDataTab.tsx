import { Button } from '@mui/material';
import { FC, useCallback, useEffect } from 'react';
import { useDeactivateAccount } from 'shared/api/generated/auth-controller/auth-controller';
import ConfirmationModal from 'shared/components/ConfirmationModal/ConfirmationModal';
import { PatientInfoPanel, PatientInfoPanelProps } from 'shared/components/PatientInfoPanel';
import { useModal } from 'shared/hooks/useModal';
import { useNotification } from 'shared/hooks/useNotification';

interface PersonalDataTabProps {
  patientInfo: PatientInfoPanelProps;
  onSuccessfulDeactivate: () => void;
}

export const PersonalDataTab: FC<PersonalDataTabProps> = ({
  patientInfo,
  onSuccessfulDeactivate,
}) => {
  const { showNotification, NotificationComponent } = useNotification();
  const { openModal } = useModal();
  const {
    mutateAsync: deactivateAccount,
    isPending: isDeactivateAccountLoading,
    isError: isDeactivateAccountError,
  } = useDeactivateAccount();

  const handleConfirmDeactivateAccount = useCallback(async () => {
    await deactivateAccount({ id: patientInfo.patientId });
    onSuccessfulDeactivate();
    showNotification('Udało się usunąć konto', 'success');
  }, [patientInfo.patientId]);

  const handleDeactivateAccountClick = () => {
    openModal('confirmAccountDeactivationModal', (close) => (
      <ConfirmationModal
        onConfirm={() => {
          handleConfirmDeactivateAccount();
          close();
        }}
        onCancel={close}
      />
    ));
  };

  useEffect(() => {
    if (isDeactivateAccountError) {
      showNotification('Coś poszło nie tak', 'error');
    }
  }, [isDeactivateAccountError]);

  return (
    <div className="flex w-1/2 flex-col gap-8">
      <PatientInfoPanel {...patientInfo} />
      <Button
        disabled={isDeactivateAccountLoading}
        loading={isDeactivateAccountLoading}
        onClick={handleDeactivateAccountClick}
        sx={{ width: 290 }}
        variant="contained"
        color="error"
      >
        Usuń dane osobowe pacjenta
      </Button>
      <NotificationComponent />
    </div>
  );
};
