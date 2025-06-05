import { CardHeader } from '@mui/material';
import { PrescriptionsTable } from 'modules/prescriptions/components/PrescriptionsTable/PrescriptionsTable';
import { FC, useEffect } from 'react';
import { useGetPrescriptionsForUser } from 'shared/api/generated/prescription-controller/prescription-controller';
import { FullPageLoadingSpinner } from 'shared/components/FullPageLoadingSpinner';
import { useAuthStore } from 'shared/hooks/stores/useAuthStore';
import { useNotification } from 'shared/hooks/useNotification';

const PrescriptionsPage: FC = () => {
  const userId = useAuthStore((state) => state.user!.id);
  const { showNotification, NotificationComponent } = useNotification();

  const {
    data: patientPrescriptions,
    isLoading: isPatientPrescriptionsLoading,
    isError: isPatientPrescriptionsError,
  } = useGetPrescriptionsForUser(userId);

  useEffect(() => {
    if (isPatientPrescriptionsError) {
      showNotification('Coś poszło nie tak', 'error');
    }
  }, [isPatientPrescriptionsError]);

  if (isPatientPrescriptionsLoading) {
    return <FullPageLoadingSpinner />;
  }

  if (isPatientPrescriptionsError) {
    return <NotificationComponent />;
  }
  if (patientPrescriptions) {
    return (
      <div className="flex flex-col">
        <CardHeader title={'Recepty'} />
        <PrescriptionsTable prescriptions={patientPrescriptions} />
        <NotificationComponent />
      </div>
    );
  }
};

export default PrescriptionsPage;
