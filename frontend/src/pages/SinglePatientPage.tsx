import { Button, CardHeader } from '@mui/material';
import { PatientTabs } from 'modules/patient/PatientTabs';
import { FC, useCallback, useEffect, useMemo, useState } from 'react';
import { useParams } from 'react-router';
import { useCreateAdditionalService } from 'shared/api/generated/additional-service-controller/additional-service-controller';
import { useGetFilesForPatient } from 'shared/api/generated/attachment-controller/attachment-controller';
import { useGetAllDoctors } from 'shared/api/generated/doctors-controller/doctors-controller';
import {
  CreateAdditionalServiceDTO,
  ScheduleVisitDTO,
  Service,
  ServiceType,
} from 'shared/api/generated/generated.schemas';
import { useGetPatientDetails } from 'shared/api/generated/patients-controller/patients-controller';
import { useGetAllServices } from 'shared/api/generated/service-controller/service-controller';
import {
  scheduleVisit,
  useGetVisitsByPatientId,
} from 'shared/api/generated/visit-controller/visit-controller';
import { AdditionalServiceModal } from 'shared/components/AdditionalServiceModal';
import { FullPageLoadingSpinner } from 'shared/components/FileUpload/FullPageLoadingSpinner';
import { ScheduleVisitModal } from 'shared/components/ScheduleVisitModal/ScheduleVisitModal';
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
    data: allDoctors,
    isLoading: isDoctorsLoading,
    isError: isDoctorsError,
  } = useGetAllDoctors();

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
    refetch: refetchPatientVisits,
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
    isPatientVisitsLoading ||
    isDoctorsLoading;
  const isInitialError =
    isServicesError ||
    isPatientInfoError ||
    isPatientAttachmentsError ||
    isPatientVisitsError ||
    isDoctorsError;

  const patientFullName = useMemo(
    () => `${patientInfo?.firstName} ${patientInfo?.lastName}`,
    [patientInfo],
  );

  const handleCreateAdditionalService = async (data: CreateAdditionalServiceDTO) => {
    await createAdditionalService({ data });
    showNotification('Zapisano', 'success');
  };

  const handleScheduleVisit = async (data: ScheduleVisitDTO) => {
    try {
      await scheduleVisit(data);
      showNotification('Zapisano', 'success');
    } catch (err) {
      console.warn('Error scheduling the visit: ', err);
      showNotification('Wystąpił bład przy umawianiu wizyty', 'error');
    }
  };

  const handleAdditionalServiceClick = useCallback(async () => {
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
  }, [openModal, closeModal, isInitialLoading, additionalServices, patientInfo, patientId]);

  const handleScheduleVisitClick = useCallback(async () => {
    if (allDoctors !== undefined && allServices !== undefined) {
      openModal(
        'scheduleVisitModal',
        <ScheduleVisitModal
          allDoctors={allDoctors}
          allServices={allServices}
          patientId={patientId}
          patientFullName={patientFullName}
          patientAge={patientInfo?.birthdate ? getAge(new Date(patientInfo?.birthdate)) : null}
          onConfirm={async (formData) => {
            await handleScheduleVisit({
              patientInformation: formData.additionalInfo,
              patientId: patientId,
              doctorId: formData.doctorId,
              firstTimeSlotId: formData.firstTimeSlotId,
              serviceId: formData.serviceId,
            });
            await refetchPatientVisits();
            closeModal('scheduleVisitModal');
          }}
          onCancel={() => closeModal('scheduleVisitModal')}
        />,
      );
    }
  }, [openModal, closeModal, allDoctors, allServices, patientInfo, patientId]);

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
        <div className="flex gap-2">
          <Button onClick={() => handleScheduleVisitClick()} variant="contained">
            Umów wizytę dla pacjenta
          </Button>
          <Button onClick={() => handleAdditionalServiceClick()} variant="contained">
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
        refetch={() => {
          refetchPatientInfo();
          refetchPatientVisits();
        }}
      />
      <NotificationComponent />
    </div>
  );
};

export default SinglePatientPage;
