import { CardHeader } from '@mui/material';
import VisitsTable from 'modules/visits/VisitsTable/VisitsTable';
import { FC, useEffect } from 'react';
import { useGetAllServices } from 'shared/api/generated/service-controller/service-controller';
import {
  useGetAllVisits,
  useGetVisitsForCurrentPatient,
} from 'shared/api/generated/visit-controller/visit-controller';
import CancelVisitModal from 'shared/components/ConfirmationModal/CancelVisitModal';
import { FullPageLoadingSpinner } from 'shared/components/FullPageLoadingSpinner';
import { useAuth } from 'shared/hooks/useAuth';
import { useModal } from 'shared/hooks/useModal';
import { useNotification } from 'shared/hooks/useNotification';

const VisitsPage: FC = () => {
  const { user, isPatient, isDoctor } = useAuth();
  const { openModal } = useModal();
  const { showNotification, NotificationComponent } = useNotification();

  if (!user || !user.id) {
    return null;
  }

  const {
    data: patientVisits,
    isLoading: isPatientVisitsLoading,
    isError: isPatientVisitsError,
  } = useGetVisitsForCurrentPatient({ query: { enabled: isPatient } });

  const {
    data: allVisits,
    isLoading: isAllVisitsLoading,
    isError: isAllVisitsError,
    refetch: refetchAllVisits,
  } = useGetAllVisits({ query: { enabled: !isPatient } });

  const {
    data: allServices,
    isLoading: isServicesLoading,
    isError: isServicesError,
  } = useGetAllServices();

  const visits = isPatient ? patientVisits : allVisits;
  const isLoading = isPatientVisitsLoading || isAllVisitsLoading || isServicesLoading;
  const isError = isPatientVisitsError || isAllVisitsError || isServicesError;

  const handleCancelVisitClick = (visitId: number) => {
    openModal('cancelVisitModal', (close) => (
      <CancelVisitModal visitId={visitId} onClose={close} onSuccess={refetchAllVisits} />
    ));
  };

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
  if (visits && allServices) {
    return (
      <div className="flex flex-col">
        <CardHeader title={'Wizyty'} />
        <VisitsTable
          visits={visits}
          allServices={allServices}
          patientId={isPatient ? user.id : undefined}
          doctorId={isDoctor ? user.id : undefined}
          onCancel={handleCancelVisitClick}
        />
      </div>
    );
  }
};

export default VisitsPage;
