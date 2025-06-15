import { Box, Button, CardHeader } from '@mui/material';
import VisitsTable from 'modules/visits/VisitsTable/VisitsTable';
import { FC, useCallback, useEffect, useState } from 'react';
import { useNavigate } from 'react-router';
import { getPatientDetails } from 'shared/api/generated/patients-controller/patients-controller';
import { useGetAllServices } from 'shared/api/generated/service-controller/service-controller';
import {
  useGetAllVisits,
  useGetVisitsForCurrentDoctor,
  useGetVisitsForCurrentPatient,
} from 'shared/api/generated/visit-controller/visit-controller';
import CancelVisitModal from 'shared/components/ConfirmationModal/CancelVisitModal';
import { FullPageLoadingSpinner } from 'shared/components/FullPageLoadingSpinner';
import { ScheduleVisitModal } from 'shared/components/ScheduleVisitModal/ScheduleVisitModal';
import { useAuth } from 'shared/hooks/useAuth';
import { useModal } from 'shared/hooks/useModal';
import { useNotification } from 'shared/hooks/useNotification';
import { useSitemap } from 'shared/hooks/useSitemap';
import { getAgeFromBirthDate } from 'shared/utils/getAgeFromBirthDate';
import { getYearAgoAsDateString } from 'shared/utils/getYearAgoAsDateString';

const VisitsPage: FC = () => {
  const { user, isPatient, isDoctor, isAdmin } = useAuth();
  const sitemap = useSitemap();
  const navigate = useNavigate();
  const { openModal } = useModal();
  const { showNotification, NotificationComponent } = useNotification();
  const [isArchivalVisitsOn, setIsArchivalVisitsOn] = useState(false);

  if (isAdmin) {
    navigate(sitemap.admin);
  }

  if (!user || !user.id) {
    return null;
  }

  const {
    data: patientVisits,
    isLoading: isPatientVisitsLoading,
    isError: isPatientVisitsError,
    refetch: refetchPatientVisits,
  } = useGetVisitsForCurrentPatient(
    {
      startDate: isArchivalVisitsOn ? getYearAgoAsDateString() : undefined,
    },
    {
      query: {
        enabled: isPatient,
        queryKey: ['visitsForCurrentPatient', isArchivalVisitsOn],
      },
    },
  );

  const {
    data: allVisits,
    isLoading: isAllVisitsLoading,
    isError: isAllVisitsError,
    refetch: refetchAllVisits,
  } = useGetAllVisits(
    {
      startDate: isArchivalVisitsOn ? getYearAgoAsDateString() : undefined,
    },
    {
      query: {
        enabled: !isPatient && !isDoctor,
        queryKey: ['allVisits', isArchivalVisitsOn],
      },
    },
  );

  const {
    data: doctorVisits,
    isLoading: isDoctorVisitsLoading,
    isError: isDoctorVisitsError,
    refetch: refetchDoctorVisits,
  } = useGetVisitsForCurrentDoctor(
    {
      startDate: isArchivalVisitsOn ? getYearAgoAsDateString() : undefined,
    },
    {
      query: {
        enabled: isDoctor,
        queryKey: ['visitsForCurrentDoctor', isArchivalVisitsOn],
      },
    },
  );

  const {
    data: allServices,
    isLoading: isServicesLoading,
    isError: isServicesError,
  } = useGetAllServices();

  const refetchVisits = async () => {
    if (isPatient) {
      await refetchPatientVisits();
      return;
    }
    if (isDoctor) {
      refetchDoctorVisits();
      return;
    }
    await refetchAllVisits();
  };

  const visits = isPatient ? patientVisits : isDoctor ? doctorVisits : allVisits;
  const isLoading =
    isPatientVisitsLoading || isAllVisitsLoading || isServicesLoading || isDoctorVisitsLoading;
  const isError =
    isPatientVisitsError || isAllVisitsError || isServicesError || isDoctorVisitsError;

  const handleCancelVisitClick = (visitId: number) => {
    openModal('cancelVisitModal', (close) => (
      <CancelVisitModal
        visitId={visitId}
        onClose={close}
        onSuccess={() => {
          refetchVisits();
          showNotification('Pomyślnie odwołano wizytę!', 'success');
        }}
      />
    ));
  };

  const handleScheduleVisitClick = useCallback(async () => {
    const patientDetails = await getPatientDetails(user.id);
    openModal('scheduleVisitModal', (close) => (
      <ScheduleVisitModal
        patientId={patientDetails.id}
        patientFullName={`${patientDetails.firstName} ${patientDetails.lastName}`}
        patientAge={
          patientDetails?.birthdate ? getAgeFromBirthDate(new Date(patientDetails.birthdate)) : null
        }
        onConfirm={async () => {
          await refetchVisits();
          close();
          showNotification('Umówiono wizytę', 'success');
        }}
        onCancel={close}
      />
    ));
  }, [openModal]);

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
        <Box
          sx={{
            display: 'flex',
            width: '100%',
            justifyContent: 'space-between',
            alignItems: 'center',
          }}
        >
          <CardHeader title={'Wizyty'} />
          {isPatient && (
            <Button
              sx={{
                alignSelf: 'center',
                backgroundColor: '#3f51b5',
                color: 'white',
                '&:hover': {
                  backgroundColor: '#283593',
                },
              }}
              onClick={handleScheduleVisitClick}
              variant="contained"
            >
              Umów wizytę
            </Button>
          )}
        </Box>

        <VisitsTable
          visits={visits}
          allServices={allServices}
          patientId={isPatient ? user.id : undefined}
          doctorId={isDoctor ? user.id : undefined}
          onCancel={handleCancelVisitClick}
          refetchVisits={refetchVisits}
          isArchivalVisitsOn={isArchivalVisitsOn}
          onArchivalModeToggle={() => setIsArchivalVisitsOn((prev) => !prev)}
        />
      </div>
    );
  }
};

export default VisitsPage;
