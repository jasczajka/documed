import { Button, CardHeader } from '@mui/material';
import { PatientTabs } from 'modules/patient/PatientTabs';
import { FC, useCallback, useEffect, useMemo, useState } from 'react';
import { useParams } from 'react-router';
import { useCreateAdditionalService } from 'shared/api/generated/additional-service-controller/additional-service-controller';
import { useGetFilesForPatient } from 'shared/api/generated/attachment-controller/attachment-controller';
import {
  CreateAdditionalServiceDTO,
  Service,
  ServiceType,
} from 'shared/api/generated/generated.schemas';
import { useGetAllServices } from 'shared/api/generated/service-controller/service-controller';
import { useGetPatientDetails } from 'shared/api/generated/user-controller/user-controller';
import { useGetVisitsByPatientId } from 'shared/api/generated/visit-controller/visit-controller';
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
    refetch: refetchPatientInfo,
  } = useGetPatientDetails(patientId);

  const {
    data: patientAttachments,
    isLoading: isPatientAttachmentsLoading,
    isError: isPatientAttachmentsError,
    refetch: refetchPatientAttachments,
  } = useGetFilesForPatient(patientId);

  const {
    data: patientVisits,
    isLoading: isPatientVisitsLoading,
    isError: isPatientVisitsError,
    // refetch: refetchPatientVisits,
  } = useGetVisitsByPatientId(patientId);

  const {
    mutateAsync: createAdditionalService,
    isError: isCreateAdditionalServiceError,
    isPending: isCreateAdditionalServiceLoading,
  } = useCreateAdditionalService();

  const isInitialLoading =
    isServicesLoading ||
    isPatientInfoLoading ||
    isPatientAttachmentsLoading ||
    isPatientVisitsLoading;
  const isInitialError =
    isServicesError || isPatientInfoError || isPatientAttachmentsError || isPatientVisitsError;

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
          onConfirm={async (data) => {
            await handleCreateAdditionalService({
              ...data,
              date: new Date().toISOString(),
              patientId,
            });
            await refetchPatientAttachments();
            closeModal('additionalServiceModal');
          }}
          onCancel={() => closeModal('additionalServiceModal')}
          loading={isCreateAdditionalServiceLoading}
        />,
      );
    }
  }, [openModal, closeModal, isInitialLoading, additionalServices]);

  const patientFullName = useMemo(
    () => `${patientInfo?.firstName} ${patientInfo?.lastName}`,
    [patientInfo],
  );

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

  if (!patientInfo || !fulfillerId || patientVisits == undefined || allServices == undefined) {
    return <NotificationComponent />;
  }

  if (isInitialLoading) {
    return <FullPageLoadingSpinner />;
  }

  return (
    <div className="flex flex-col">
      <div className="flex w-full items-center justify-between">
        <CardHeader title={patientFullName} />
        <div className="flex-shrink-0">
          <Button onClick={() => {}} variant="contained">
            Umów wizytę dla pacjenta
          </Button>
          <Button onClick={() => handleScheduleVisitClick()} variant="contained">
            Rozpocznij usługę dodatkową
          </Button>
        </div>
      </div>
      <PatientTabs
        patientInfo={{
          patientId: patientInfo.id,
          patientFullName,
          patientAge: getAge(new Date(patientInfo.birthdate)),
        }}
        tabIndex={tabIndex}
        onTabChange={onTabChange}
        patientAttachments={patientAttachments ?? []}
        patientVisits={patientVisits}
        allServices={allServices}
        refetch={() => refetchPatientInfo()}
      />
      <NotificationComponent />
    </div>
  );
};

export default SinglePatientPage;
