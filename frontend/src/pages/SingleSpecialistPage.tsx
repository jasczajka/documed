import { CardHeader } from '@mui/material';
import { SpecialistTabs } from 'modules/specialist/SpecialistTabs';
import { mapFromReturnWorkTimes } from 'modules/specialist/utils';
import { FC, useCallback, useEffect, useState } from 'react';
import { useParams } from 'react-router';
import {
  useGetDoctorDetails,
  useUpdateDoctorSpecializations,
} from 'shared/api/generated/doctors-controller/doctors-controller';
import { Specialization, UploadWorkTimeDTO } from 'shared/api/generated/generated.schemas';
import { useGetVisitsByDoctorId } from 'shared/api/generated/visit-controller/visit-controller';

import {
  useGetWorkTimesForUser,
  useUpdateWorkTimesForUser,
} from 'shared/api/generated/work-time-controller/work-time-controller';
import CancelVisitModal from 'shared/components/ConfirmationModal/CancelVisitModal';
import { FullPageLoadingSpinner } from 'shared/components/FullPageLoadingSpinner';
import { useDoctorsStore } from 'shared/hooks/stores/useDoctorsStore';
import { useSpecializationsStore } from 'shared/hooks/stores/useSpecializationsStore';
import { useModal } from 'shared/hooks/useModal';
import { useNotification } from 'shared/hooks/useNotification';

const SingleSpecialistPage: FC = () => {
  const { id } = useParams();
  const doctorId = Number(id);
  const allSpecializations = useSpecializationsStore((state) => state.specializations);
  const refetchDoctors = useDoctorsStore((state) => state.fetchDoctors);
  const [specializations, setSpecializations] = useState<Specialization[]>([]);
  const [tabIndex, setTabIndex] = useState(0);
  const { showNotification, NotificationComponent } = useNotification();
  const { openModal } = useModal();

  const onTabChange = useCallback((index: number) => {
    setTabIndex(index);
  }, []);

  const {
    data: doctorInfo,
    isLoading: isDoctorInfoLoading,
    isError: isDoctorInfoError,
  } = useGetDoctorDetails(doctorId);

  const {
    data: doctorWorkTimes,
    isLoading: isDoctorWorkTimesLoading,
    isError: isDoctorWorkTimesError,
    refetch: refetchDoctorWorkTimes,
  } = useGetWorkTimesForUser(doctorId);

  const {
    data: doctorVisits,
    isLoading: isDoctorVisitsLoading,
    isError: isDoctorVisitsError,
    refetch: refetchDoctorVisits,
  } = useGetVisitsByDoctorId(doctorId);

  const {
    mutateAsync: updateDoctorSpecializations,
    isPending: isUpdateDoctorSpecializationsLoading,
    isError: isUpdateDoctorSpecializationsError,
  } = useUpdateDoctorSpecializations();

  const {
    mutateAsync: updateDoctorWorkTimes,
    isPending: isUpdateDoctorWorkTimesLoading,
    isError: isUpdateDoctorWorkTimesError,
  } = useUpdateWorkTimesForUser();

  const handleUpdateSpecialistSpecializations = async (
    updatedSpecializations: Specialization[],
  ) => {
    const specializationIds = updatedSpecializations.map((spec) => spec.id);
    await updateDoctorSpecializations({ id: doctorId, data: { specializationIds } });
    await refetchDoctors();
    showNotification('Pomyślnie zaktualizowano specjalizacje!', 'success');
    setSpecializations(updatedSpecializations);
  };

  const handleUpdateSpecialistWorkTimes = async (updatedWorkTimes: UploadWorkTimeDTO[]) => {
    console.log('work times in handler: ', updatedWorkTimes);
    await updateDoctorWorkTimes({ userId: doctorId, data: updatedWorkTimes });
    await refetchDoctorWorkTimes();
    showNotification('Pomyślnie zaktualizowano godziny pracy!', 'success');
  };

  const handleCancelVisitClick = (visitId: number) => {
    openModal('cancelVisitModal', (close) => (
      <CancelVisitModal visitId={visitId} onClose={close} onSuccess={refetchDoctorVisits} />
    ));
  };

  const isInitialLoading = isDoctorInfoLoading || isDoctorWorkTimesLoading || isDoctorVisitsLoading;
  const isLoading = isUpdateDoctorSpecializationsLoading || isUpdateDoctorWorkTimesLoading;
  const isInitialError = isDoctorInfoError || isDoctorWorkTimesError || isDoctorVisitsError;

  useEffect(() => {
    if (isInitialError) {
      showNotification('Coś poszło nie tak', 'error');
    }
    if (isUpdateDoctorSpecializationsError) {
      showNotification('Nie udało się zaktualizować specjalizacji lekarza', 'error');
    }
    if (isUpdateDoctorWorkTimesError) {
      showNotification('Nie udało się zaktualizować godzin pracy lekarza', 'error');
    }
    if (doctorInfo) {
      setSpecializations(doctorInfo.specializations);
    }
  }, [
    isInitialError,
    doctorInfo,
    doctorWorkTimes,
    isUpdateDoctorSpecializationsError,
    isUpdateDoctorWorkTimesError,
  ]);

  if (!doctorId) {
    return null;
  }

  if (isInitialLoading) {
    return <FullPageLoadingSpinner />;
  }

  if (doctorInfo && allSpecializations && doctorVisits) {
    return (
      <div className="flex flex-col">
        <CardHeader title={`dr. ${doctorInfo?.firstName} ${doctorInfo?.lastName}`} />
        <SpecialistTabs
          doctorId={doctorId}
          doctorVisits={doctorVisits}
          refetchDoctorVisits={async () => {
            await refetchDoctorVisits();
          }}
          currentSpecializations={specializations}
          currentWorkTimes={mapFromReturnWorkTimes(doctorWorkTimes ?? [])}
          allSpecializations={allSpecializations}
          handleUpdateSpecialistSpecializations={handleUpdateSpecialistSpecializations}
          handleUpdateSpecialistWorkTimes={handleUpdateSpecialistWorkTimes}
          handleCancelVisit={handleCancelVisitClick}
          tabIndex={tabIndex}
          onTabChange={onTabChange}
          loading={isLoading}
        />
        <NotificationComponent />
      </div>
    );
  }
};

export default SingleSpecialistPage;
