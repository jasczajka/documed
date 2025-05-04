import { CardHeader } from '@mui/material';
import { SpecialistTabs } from 'modules/specialist/SpecialistTabs';
import { FC, useCallback, useEffect, useState } from 'react';
import { useParams } from 'react-router';
import { Specialization } from 'shared/api/generated/generated.schemas';
import { useGetAllSpecializations } from 'shared/api/generated/specialization-controller/specialization-controller';
import {
  useGetDoctorDetails,
  useUpdateDoctorSpecializations,
} from 'shared/api/generated/user-controller/user-controller';
import { FullPageLoadingSpinner } from 'shared/components/FileUpload/FullPageLoadingSpinner';
import { useNotification } from 'shared/hooks/useNotification';

const SingleSpecialistPage: FC = () => {
  const { id } = useParams();
  const doctorId = Number(id);
  const [specializations, setSpecializations] = useState<Specialization[]>([]);
  const { showNotification, NotificationComponent } = useNotification();

  const {
    data: allSpecializations,
    isLoading: isSpecializationsLoading,
    isError: isSpecializationsError,
  } = useGetAllSpecializations();

  const {
    data: doctorInfo,
    isLoading: isDoctorInfoLoading,
    isError: isDoctorInfoError,
  } = useGetDoctorDetails(doctorId);

  const {
    mutateAsync: updateDoctorSpecializations,
    isPending: isUpdateDoctorSpecializationsLoading,
  } = useUpdateDoctorSpecializations();

  const handleUpdateSpecialistSpecializations = useCallback(
    async (updatedSpecializations: Specialization[]) => {
      const specializationIds = updatedSpecializations.map((spec) => spec.id);
      await updateDoctorSpecializations({ id: doctorId, data: { specializationIds } });
      setSpecializations(updatedSpecializations);
    },
    [],
  );

  const isLoading = isSpecializationsLoading || isDoctorInfoLoading;
  const isError = isSpecializationsError || isDoctorInfoError;

  useEffect(() => {
    if (isError) {
      showNotification('Coś poszło nie tak przy pobieraniu danych', 'error');
    }
    if (doctorInfo) {
      setSpecializations(doctorInfo.specializations);
    }
  }, [isError, doctorInfo]);

  if (!doctorId) {
    return null;
  }

  if (isLoading) {
    return <FullPageLoadingSpinner />;
  }

  if (doctorInfo && allSpecializations) {
    return (
      <div className="flex flex-col">
        <CardHeader title={`dr. ${doctorInfo?.firstName} ${doctorInfo?.lastName}`} />
        <SpecialistTabs
          doctorId={doctorId}
          currentSpecializations={specializations}
          allSpecializations={allSpecializations}
          handleUpdateSpecialistSpecializations={handleUpdateSpecialistSpecializations}
          updateSpecialistSpecializationsLoading={isUpdateDoctorSpecializationsLoading}
        />
        <NotificationComponent />
      </div>
    );
  }
};

export default SingleSpecialistPage;
