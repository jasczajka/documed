import { CardHeader } from '@mui/material';
import PatientsTable from 'modules/patients/PatientsTable/PatientsTable';
import { FC, useEffect } from 'react';
import { useGetAllPatientsDetails } from 'shared/api/generated/patients-controller/patients-controller';
import { FullPageLoadingSpinner } from 'shared/components/FullPageLoadingSpinner';
import { useNotification } from 'shared/hooks/useNotification';

const PatientsPage: FC = () => {
  const { showNotification, NotificationComponent } = useNotification();

  const {
    data: allPatients,
    isLoading: isAllPatientsLoading,
    isError: isAllPatientsError,
  } = useGetAllPatientsDetails();

  useEffect(() => {
    if (isAllPatientsError) {
      showNotification('Coś poszło nie tak', 'error');
    }
  }, [isAllPatientsError]);
  if (isAllPatientsLoading) {
    return <FullPageLoadingSpinner />;
  }

  if (isAllPatientsError) {
    return <NotificationComponent />;
  }

  if (allPatients) {
    return (
      <div className="flex flex-col">
        <CardHeader title={'Pacjenci'} />
        <PatientsTable patients={allPatients} />
      </div>
    );
  }
};

export default PatientsPage;
