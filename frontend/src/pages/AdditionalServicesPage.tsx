import { CardHeader } from '@mui/material';
import AdditionalServicesTable from 'modules/additionalServices/additionalServicesTable/AdditionalServicesTable';
import { FC, useEffect, useMemo } from 'react';
import {
  useGetAdditionalServicesByPatient,
  useGetAllAdditionalServices,
} from 'shared/api/generated/additional-service-controller/additional-service-controller';
import { ServiceType } from 'shared/api/generated/generated.schemas';
import { useGetAllServices } from 'shared/api/generated/service-controller/service-controller';
import { FullPageLoadingSpinner } from 'shared/components/FullPageLoadingSpinner';
import { useAuth } from 'shared/hooks/useAuth';
import { useNotification } from 'shared/hooks/useNotification';

const AdditionalServicesPage: FC = () => {
  const { user, isPatient, isDoctor } = useAuth();
  const { showNotification, NotificationComponent } = useNotification();

  if (!user || !user.id) {
    return null;
  }

  const {
    data: patientAdditionalServices,
    isLoading: isPatientAdditionalServicesLoading,
    isError: isPatientAdditionalServicesError,
    refetch: refetchPatientAdditionalServices,
  } = useGetAdditionalServicesByPatient(user.id, { query: { enabled: isPatient } });

  const {
    data: allAdditionalServiceInstances,
    isLoading: isAllAdditionalServiceInstancesLoading,
    isError: isAllAdditionalServiceInstancesError,
    refetch: refetchAllAdditionalServiceInstances,
  } = useGetAllAdditionalServices({ query: { enabled: !isPatient } });

  const {
    data: allServices,
    isLoading: isServicesLoading,
    isError: isServicesError,
  } = useGetAllServices();

  const additionalServices = isPatient ? patientAdditionalServices : allAdditionalServiceInstances;
  const allAdditionalServices = useMemo(
    () => allServices?.filter((service) => service.type === ServiceType.ADDITIONAL_SERVICE),
    [allServices],
  );
  const isLoading =
    isPatientAdditionalServicesLoading ||
    isAllAdditionalServiceInstancesLoading ||
    isServicesLoading ||
    isServicesLoading;
  const isError =
    isPatientAdditionalServicesError || isAllAdditionalServiceInstancesError || isServicesError;

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
  if (additionalServices && allAdditionalServices) {
    return (
      <div className="flex flex-col">
        <CardHeader title={'Usługi dodatkowe'} />
        <AdditionalServicesTable
          additionalServices={additionalServices}
          allAdditionalServices={allAdditionalServices}
          patientId={isPatient ? user.id : undefined}
          doctorId={isDoctor ? user.id : undefined}
          refetch={async () => {
            if (isPatient) {
              refetchPatientAdditionalServices();
              return;
            }
            refetchAllAdditionalServiceInstances();
          }}
        />
      </div>
    );
  }
};

export default AdditionalServicesPage;
