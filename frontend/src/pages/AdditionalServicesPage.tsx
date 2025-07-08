import { CardHeader } from '@mui/material';
import AdditionalServicesTable from 'modules/additionalServices/additionalServicesTable/AdditionalServicesTable';
import { FC, useEffect, useState } from 'react';
import {
  useGetAdditionalServicesByPatient,
  useGetAllAdditionalServices,
} from 'shared/api/generated/additional-service-controller/additional-service-controller';
import { FullPageLoadingSpinner } from 'shared/components/FullPageLoadingSpinner';
import { useAuth } from 'shared/hooks/useAuth';
import { useNotification } from 'shared/hooks/useNotification';
import { getYearAgoAsDateString } from 'shared/utils/getYearAgoAsDateString';

const AdditionalServicesPage: FC = () => {
  const { user, isPatient, isDoctor } = useAuth();
  const { showNotification, NotificationComponent } = useNotification();
  const [isArchivalModeOn, setIsArchivalModeOn] = useState(false);

  if (!user || !user.id) {
    return null;
  }

  const {
    data: patientAdditionalServices,
    isLoading: isPatientAdditionalServicesLoading,
    isError: isPatientAdditionalServicesError,
    refetch: refetchPatientAdditionalServices,
  } = useGetAdditionalServicesByPatient(
    user.id,
    {
      startDate: isArchivalModeOn ? getYearAgoAsDateString() : undefined,
    },
    {
      query: {
        enabled: isPatient,
        queryKey: ['additionalServicesForPatient', isArchivalModeOn],
      },
    },
  );

  const {
    data: allAdditionalServiceInstances,
    isLoading: isAllAdditionalServiceInstancesLoading,
    isError: isAllAdditionalServiceInstancesError,
    refetch: refetchAllAdditionalServiceInstances,
  } = useGetAllAdditionalServices(
    {
      startDate: isArchivalModeOn ? getYearAgoAsDateString() : undefined,
    },
    {
      query: {
        enabled: !isPatient,
        queryKey: ['additionalServices', isArchivalModeOn],
      },
    },
  );

  const additionalServices = isPatient ? patientAdditionalServices : allAdditionalServiceInstances;

  const isLoading = isPatientAdditionalServicesLoading || isAllAdditionalServiceInstancesLoading;
  const isError = isPatientAdditionalServicesError || isAllAdditionalServiceInstancesError;

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
  if (additionalServices) {
    return (
      <div className="flex flex-col">
        <CardHeader title={'Usługi dodatkowe'} />
        <AdditionalServicesTable
          additionalServices={additionalServices}
          patientId={isPatient ? user.id : undefined}
          doctorId={isDoctor ? user.id : undefined}
          refetch={async () => {
            if (isPatient) {
              refetchPatientAdditionalServices();
              return;
            }

            refetchAllAdditionalServiceInstances();
          }}
          isArchivalAdditionalServicesOn={isArchivalModeOn}
          onArchivalModeToggle={() => setIsArchivalModeOn((prev) => !prev)}
          displayPatientColumn={!isPatient}
        />
      </div>
    );
  }
};

export default AdditionalServicesPage;
