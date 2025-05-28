import { Button, CardHeader } from '@mui/material';
import { PatientTabs } from 'modules/patient/PatientTabs';
import { FC, useCallback, useEffect, useMemo, useState } from 'react';
import { useParams } from 'react-router';
import { useGetAdditionalServicesByPatient } from 'shared/api/generated/additional-service-controller/additional-service-controller';
import { useGetFilesForPatient } from 'shared/api/generated/attachment-controller/attachment-controller';
import { useGetAllDoctors } from 'shared/api/generated/doctors-controller/doctors-controller';
import { ServiceType } from 'shared/api/generated/generated.schemas';
import { useGetPatientDetails } from 'shared/api/generated/patients-controller/patients-controller';
import { useGetAllServices } from 'shared/api/generated/service-controller/service-controller';
import { useGetVisitsByPatientId } from 'shared/api/generated/visit-controller/visit-controller';
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
    data: patientAdditionalServices,
    isLoading: isPatientAdditionalServicesLoading,
    isError: isPatientAdditionalServicesError,
    refetch: refetchPatientAdditionalServices,
  } = useGetAdditionalServicesByPatient(patientId);

  const isInitialLoading =
    isServicesLoading ||
    isPatientInfoLoading ||
    isPatientAttachmentsLoading ||
    isPatientVisitsLoading ||
    isDoctorsLoading ||
    isPatientAdditionalServicesLoading;
  const isInitialError =
    isServicesError ||
    isPatientInfoError ||
    isPatientAttachmentsError ||
    isPatientVisitsError ||
    isDoctorsError ||
    isPatientAdditionalServicesError;

  const patientFullName = useMemo(
    () => `${patientInfo?.firstName} ${patientInfo?.lastName}`,
    [patientInfo],
  );

  const allAdditionalServices = useMemo(() => {
    if (!allServices) {
      return [];
    }
    return allServices.filter((service) => service.type === ServiceType.ADDITIONAL_SERVICE);
  }, [allServices]);

  const handleAdditionalServiceClick = useCallback(async () => {
    if (fulfillerId !== undefined && patientId !== undefined && allServices !== undefined) {
      openModal(
        'createAdditionalServiceModal',
        <AdditionalServiceModal
          allAdditionalServices={allAdditionalServices}
          patientId={patientId}
          fulfillerId={fulfillerId}
          patientFullName={`${patientInfo?.firstName} ${patientInfo?.lastName}`}
          patientAge={patientInfo?.birthdate ? getAge(new Date(patientInfo?.birthdate)) : null}
          onConfirm={async () => {
            closeModal('additionalServiceModal');
            showNotification('Zapisano dane usługi dodatkowej', 'success');

            await refetchPatientAttachments();
            await refetchPatientAdditionalServices();
          }}
          onCancel={() => closeModal('createAdditionalServiceModal')}
          mode="create"
        />,
      );
    }
  }, [openModal, closeModal, isInitialLoading, allServices, patientInfo, patientId]);

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
          onConfirm={async () => {
            await refetchPatientVisits();
            closeModal('scheduleVisitModal');
            showNotification('Umówiono wizytę', 'success');
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
    if (allServices) {
      console.log(allServices.filter((service) => service.type === ServiceType.ADDITIONAL_SERVICE));
    }
  }, [allServices, isInitialError]);

  if (isInitialLoading) {
    return <FullPageLoadingSpinner />;
  }

  if (
    !patientInfo ||
    !fulfillerId ||
    patientVisits === undefined ||
    allServices === undefined ||
    patientAdditionalServices === undefined
  ) {
    return <NotificationComponent />;
  }

  return (
    <div className="flex flex-col">
      <div className="flex w-full items-center justify-between">
        <CardHeader title={patientFullName} />
        <div className="flex gap-2">
          <Button onClick={handleScheduleVisitClick} variant="contained">
            Umów wizytę dla pacjenta
          </Button>
          <Button onClick={handleAdditionalServiceClick} variant="contained">
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
        patientAdditionalServices={patientAdditionalServices}
        allServices={allServices}
        allAdditionalServices={allAdditionalServices}
        refetchVisits={async () => {
          await refetchPatientVisits();
        }}
        refetchAdditionalServices={async () => {
          await refetchPatientAdditionalServices();
        }}
        refetchPatientInfo={async () => {
          await refetchPatientInfo();
        }}
      />
      <NotificationComponent />
    </div>
  );
};

export default SinglePatientPage;
