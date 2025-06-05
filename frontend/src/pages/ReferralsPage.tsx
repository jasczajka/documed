import { CardHeader } from '@mui/material';
import { ReferralsTable } from 'modules/referrals/components/ReferralsTable';
import { FC, useEffect } from 'react';
import { useGetAllReferralsForPatient } from 'shared/api/generated/referral-controller/referral-controller';
import { FullPageLoadingSpinner } from 'shared/components/FullPageLoadingSpinner';
import { useAuthStore } from 'shared/hooks/stores/useAuthStore';
import { useNotification } from 'shared/hooks/useNotification';

const ReferralsPage: FC = () => {
  const userId = useAuthStore((state) => state.user!.id);
  const { showNotification, NotificationComponent } = useNotification();

  const {
    data: patientReferrals,
    isLoading: isPatientReferralsLoading,
    isError: isPatientReferralsError,
  } = useGetAllReferralsForPatient(userId);

  useEffect(() => {
    if (isPatientReferralsError) {
      showNotification('Coś poszło nie tak', 'error');
    }
  }, [isPatientReferralsError]);

  if (isPatientReferralsLoading) {
    return <FullPageLoadingSpinner />;
  }

  if (isPatientReferralsError) {
    return <NotificationComponent />;
  }
  if (patientReferrals) {
    return (
      <div className="flex flex-col">
        <CardHeader title={'Skierowania'} />
        <ReferralsTable referrals={patientReferrals} />
        <NotificationComponent />
      </div>
    );
  }
};

export default ReferralsPage;
