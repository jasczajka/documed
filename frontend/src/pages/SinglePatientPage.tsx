import { Button, CardHeader } from '@mui/material';
import { PatientTabs } from 'modules/patient/PatientTabs';
import { FC, useCallback, useEffect, useMemo, useState } from 'react';
import { useParams } from 'react-router';
import { useGetAdditionalServicesByPatient } from 'shared/api/generated/additional-service-controller/additional-service-controller';
import { useGetFilesForPatient } from 'shared/api/generated/attachment-controller/attachment-controller';
import { ServiceType } from 'shared/api/generated/generated.schemas';
import { useGetPatientDetails } from 'shared/api/generated/patients-controller/patients-controller';
import { useGetPrescriptionsForUser } from 'shared/api/generated/prescription-controller/prescription-controller';
import { useGetAllReferralsForPatient } from 'shared/api/generated/referral-controller/referral-controller';
import { useGetVisitsByPatientId } from 'shared/api/generated/visit-controller/visit-controller';
import { AdditionalServiceModal } from 'shared/components/AdditionalServiceModal';
import { FullPageLoadingSpinner } from 'shared/components/FullPageLoadingSpinner';
import { ScheduleVisitModal } from 'shared/components/ScheduleVisitModal/ScheduleVisitModal';
import { useAllServicesStore } from 'shared/hooks/stores/useAllServicesStore';
import { useAuthStore } from 'shared/hooks/stores/useAuthStore';
import { useAuth } from 'shared/hooks/useAuth';
import { useModal } from 'shared/hooks/useModal';
import { useNotification } from 'shared/hooks/useNotification';
import { getAgeFromBirthDate } from 'shared/utils/getAgeFromBirthDate';
import { getYearAgoAsDateString } from 'shared/utils/getYearAgoAsDateString';

const SinglePatientPage: FC = () => {
  const { id } = useParams();
  const { isNurse, isDoctor, isWardClerk } = useAuth();
  const patientId = Number(id);
  const [isArchivalModeOn, setIsArchivalModeOn] = useState(false);
  const fulfillerId = useAuthStore((state) => state.user?.id);
  const allServices = useAllServicesStore((state) => state.allServices);

  const allAdditionalServices = allServices.filter(
    (service) => service.type === ServiceType.ADDITIONAL_SERVICE,
  );

  const [tabIndex, setTabIndex] = useState(0);

  const onTabChange = useCallback((index: number) => {
    setTabIndex(index);
  }, []);

  const { openModal } = useModal();
  const { showNotification, NotificationComponent } = useNotification();

  const {
    data: patientDetails,
    isLoading: isPatientDetailsLoading,
    isError: isPatientDetailsError,
    refetch: refetchPatientDetails,
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
  } = useGetVisitsByPatientId(
    patientId,
    {
      startDate: isArchivalModeOn ? getYearAgoAsDateString() : undefined,
    },
    { query: { queryKey: ['visitsForPatient', isArchivalModeOn] } },
  );

  const {
    data: patientAdditionalServices,
    isLoading: isPatientAdditionalServicesLoading,
    isError: isPatientAdditionalServicesError,
    refetch: refetchPatientAdditionalServices,
  } = useGetAdditionalServicesByPatient(
    patientId,
    {
      startDate: isArchivalModeOn ? getYearAgoAsDateString() : undefined,
    },
    { query: { queryKey: ['additionalServicesForPatient', isArchivalModeOn] } },
  );

  const {
    data: patientReferrals,
    isLoading: isPatientReferralsLoading,
    isError: isPatientReferralsError,
  } = useGetAllReferralsForPatient(patientId);

  const {
    data: patientPrescriptions,
    isLoading: isPatientPrescriptionsLoading,
    isError: isPatientPrescriptionsError,
  } = useGetPrescriptionsForUser(patientId);

  const isInitialLoading =
    isPatientDetailsLoading ||
    isPatientAttachmentsLoading ||
    isPatientVisitsLoading ||
    isPatientAdditionalServicesLoading ||
    isPatientReferralsLoading ||
    isPatientPrescriptionsLoading;

  const isInitialError =
    isPatientDetailsError ||
    isPatientAttachmentsError ||
    isPatientVisitsError ||
    isPatientAdditionalServicesError ||
    isPatientReferralsError ||
    isPatientPrescriptionsError;

  const patientFullName = useMemo(
    () => `${patientDetails?.firstName} ${patientDetails?.lastName}`,
    [patientDetails],
  );

  const handleAdditionalServiceClick = useCallback(async () => {
    if (fulfillerId !== undefined && patientId !== undefined && allServices !== undefined) {
      openModal('createAdditionalServiceModal', (close) => (
        <AdditionalServiceModal
          allAdditionalServices={allAdditionalServices}
          patientPesel={patientDetails?.pesel}
          patientId={patientId}
          fulfillerId={fulfillerId}
          patientFullName={`${patientDetails?.firstName} ${patientDetails?.lastName}`}
          patientAge={
            patientDetails?.birthdate
              ? getAgeFromBirthDate(new Date(patientDetails.birthdate))
              : null
          }
          onConfirm={async () => {
            close();
            showNotification('Zapisano dane usługi dodatkowej', 'success');
            await refetchPatientAttachments();
            await refetchPatientAdditionalServices();
          }}
          onCancel={close}
          mode="create"
        />
      ));
    }
  }, [
    openModal,
    allServices,
    allAdditionalServices,
    patientDetails,
    patientId,
    fulfillerId,
    refetchPatientAttachments,
    refetchPatientAdditionalServices,
  ]);

  const handleScheduleVisitClick = useCallback(async () => {
    openModal('scheduleVisitModal', (close) => (
      <ScheduleVisitModal
        patientId={patientId}
        patientFullName={patientFullName}
        patientAge={
          patientDetails?.birthdate ? getAgeFromBirthDate(new Date(patientDetails.birthdate)) : null
        }
        onConfirm={async () => {
          await refetchPatientVisits();
          close();
          showNotification('Umówiono wizytę', 'success');
        }}
        onCancel={close}
      />
    ));
  }, [openModal, patientId, patientDetails, patientFullName, refetchPatientVisits]);

  useEffect(() => {
    if (isInitialError) {
      showNotification('Coś poszło nie tak', 'error');
    }
  }, [isInitialError]);

  if (isInitialLoading) {
    return <FullPageLoadingSpinner />;
  }

  if (
    !patientDetails ||
    !fulfillerId ||
    patientVisits === undefined ||
    allServices === undefined ||
    patientAdditionalServices === undefined ||
    patientPrescriptions === undefined ||
    patientReferrals === undefined
  ) {
    return <NotificationComponent />;
  }

  return (
    <div className="flex flex-col">
      <div className="flex w-full items-center justify-between">
        <CardHeader title={patientFullName} />
        <div className="flex gap-2">
          {isWardClerk && (
            <Button onClick={handleScheduleVisitClick} variant="contained">
              Umów wizytę dla pacjenta
            </Button>
          )}

          {(isNurse || isDoctor) && (
            <Button onClick={handleAdditionalServiceClick} variant="contained">
              Rozpocznij usługę dodatkową
            </Button>
          )}
        </div>
      </div>
      <PatientTabs
        patientDetails={patientDetails}
        patientSubscriptionId={patientDetails.subscriptionId ?? null}
        tabIndex={tabIndex}
        onTabChange={onTabChange}
        patientAttachments={patientAttachments ?? []}
        patientVisits={patientVisits}
        patientAdditionalServices={patientAdditionalServices}
        patientPrescriptions={patientPrescriptions}
        patientReferrals={patientReferrals}
        allServices={allServices}
        allAdditionalServices={allAdditionalServices}
        refetchVisits={async () => {
          await refetchPatientVisits();
        }}
        refetchAdditionalServices={async () => {
          await refetchPatientAdditionalServices();
        }}
        refetchPatientInfo={async () => {
          await refetchPatientDetails();
        }}
        isArchivalModeOn={isArchivalModeOn}
        onArchivalModeToggle={() => setIsArchivalModeOn((prev) => !prev)}
      />
      <NotificationComponent />
    </div>
  );
};

export default SinglePatientPage;
