import { CardHeader } from '@mui/material';
import { PrescriptionsTable } from 'modules/prescriptions/PrescriptionsTable/PrescriptionsTable';
import { FC, useEffect } from 'react';
import {
  useGetAllPrescriptions,
  useGetPrescriptionsForUser,
} from 'shared/api/generated/prescription-controller/prescription-controller';
import { FullPageLoadingSpinner } from 'shared/components/FullPageLoadingSpinner';
import { useAuth } from 'shared/hooks/useAuth';
import { useNotification } from 'shared/hooks/useNotification';

const PrescriptionsPage: FC = () => {
  const { user, isPatient } = useAuth();
  const { showNotification, NotificationComponent } = useNotification();

  if (!user || !user.id) {
    return null;
  }

  const {
    data: patientPrescriptions,
    isLoading: isPatientLoading,
    isError: isPatientError,
  } = useGetPrescriptionsForUser(user.id, { query: { enabled: isPatient } });

  const {
    data: allPrescriptions,
    isLoading: isAllLoading,
    isError: isAllError,
  } = useGetAllPrescriptions({ query: { enabled: !isPatient } });

  const prescriptions = isPatient ? patientPrescriptions : allPrescriptions;
  const isLoading = isPatient ? isPatientLoading : isAllLoading;
  const isError = isPatientError || isAllError;

  useEffect(() => {
    if (isError) {
      showNotification('Coś poszło nie tak', 'error');
    }
  }, [isError]);

  if (isLoading) {
    return <FullPageLoadingSpinner />;
  }

  if (isError) {
    return <NotificationComponent />;
  }
  if (prescriptions) {
    return (
      <div className="flex flex-col">
        <CardHeader title={'Recepty'} />
        <PrescriptionsTable prescriptions={prescriptions} />
        <NotificationComponent />
      </div>
    );
  }
};

export default PrescriptionsPage;
