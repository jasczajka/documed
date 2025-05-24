import { Button, CardHeader } from '@mui/material';
import { PatientTabs } from 'modules/patient/PatientTabs';
import { FC, useCallback, useEffect, useState } from 'react';
import { useParams } from 'react-router';
import { useCreateAdditionalService } from 'shared/api/generated/additional-service-controller/additional-service-controller';
import {
  CreateAdditionalServiceDTO,
  Service,
  ServiceType,
} from 'shared/api/generated/generated.schemas';
import { useGetAllServices } from 'shared/api/generated/service-controller/service-controller';
import { useGetPatientDetails } from 'shared/api/generated/user-controller/user-controller';
import { AdditionalServiceModal } from 'shared/components/AdditionalServiceModal';
import { FullPageLoadingSpinner } from 'shared/components/FileUpload/FullPageLoadingSpinner';
import { useAuthStore } from 'shared/hooks/stores/useAuthStore';
import { useModal } from 'shared/hooks/useModal';
import { useNotification } from 'shared/hooks/useNotification';
import { getAge } from 'shared/utils/getAgeFromBirthDate';

const SinglePatientPage: FC = () => {
  const { id } = useParams();
  const patientId = Number(id);
  const fulfillerId = useAuthStore((state) => state.user?.id);

  const [additionalServices, setAdditionalServices] = useState<Service[]>([]);
  const [tabIndex, setTabIndex] = useState(0);

  const onTabChange = useCallback((index: number) => {
    setTabIndex(index);
  }, []);

  const { openModal, closeModal } = useModal();
  const { showNotification, NotificationComponent } = useNotification();

  const {
    data: allServices,
    isLoading: isServicesLoading,
    isError: isServicesError,
  } = useGetAllServices();

  const {
    data: patientInfo,
    isLoading: isPatientInfoLoading,
    isError: isPatientInfoError,
  } = useGetPatientDetails(patientId);

  const {
    mutateAsync: createAdditionalService,
    isError: isCreateAdditionalServiceError,
    isPending: isCreateAdditionalServiceLoading,
  } = useCreateAdditionalService();

  const isInitialLoading = isServicesLoading || isPatientInfoLoading;
  const isInitialError = isServicesError || isPatientInfoError;

  const handleCreateAdditionalService = async (data: CreateAdditionalServiceDTO) => {
    await createAdditionalService({ data });
    showNotification('Zapisano', 'success');
  };

  const handleScheduleVisitClick = useCallback(async () => {
    if (fulfillerId && patientId) {
      openModal(
        'additionalServiceModal',
        <AdditionalServiceModal
          allAdditionalServices={additionalServices}
          patientId={patientId}
          fulfillerId={fulfillerId}
          patientFullName={`${patientInfo?.firstName} ${patientInfo?.lastName}`}
          patientAge={patientInfo?.birthdate ? getAge(new Date(patientInfo?.birthdate)) : null}
          onConfirm={(data) => {
            handleCreateAdditionalService({ ...data, date: new Date().toISOString(), patientId });
            closeModal('additionalServiceModal');
          }}
          onCancel={() => closeModal('additionalServiceModal')}
          loading={isCreateAdditionalServiceLoading}
        />,
      );
    }
  }, [openModal, closeModal, isInitialLoading, additionalServices]);

  useEffect(() => {
    if (isInitialError) {
      showNotification('Coś poszło nie tak', 'error');
    }
    if (isCreateAdditionalServiceError) {
      showNotification('Nie udało zapisać się danych dodatkowej usługi', 'error');
    }
    if (allServices) {
      console.log(allServices.filter((service) => service.type === ServiceType.ADDITIONAL_SERVICE));
      setAdditionalServices(
        allServices.filter((service) => service.type === ServiceType.ADDITIONAL_SERVICE),
      );
    }
  }, [allServices, isInitialError]);

  if (!patientInfo || !fulfillerId) {
    return <NotificationComponent />;
  }

  if (isInitialLoading) {
    return <FullPageLoadingSpinner />;
  }

  return (
    <div className="flex flex-col">
      <div className="flex w-full items-center justify-between">
        <CardHeader title={`${patientInfo?.firstName} ${patientInfo?.lastName}`} />
        <div className="flex-shrink-0">
          <Button onClick={() => handleScheduleVisitClick()} variant="contained">
            Rozpocznij usługę dodatkową
          </Button>
        </div>
      </div>
      <PatientTabs patientId={patientId} tabIndex={tabIndex} onTabChange={onTabChange} />
      <NotificationComponent />
    </div>
  );
};

export default SinglePatientPage;
