import { CardHeader } from '@mui/material';
import { ReferralsTable } from 'modules/referrals/components/ReferralsTable';
import { FC, useEffect } from 'react';
import {
  useGetAllReferrals,
  useGetAllReferralsForPatient,
} from 'shared/api/generated/referral-controller/referral-controller';
import { FullPageLoadingSpinner } from 'shared/components/FullPageLoadingSpinner';
import { useAuth } from 'shared/hooks/useAuth';
import { useNotification } from 'shared/hooks/useNotification';

const ReferralsPage: FC = () => {
  const { user, isPatient } = useAuth();
  const { showNotification, NotificationComponent } = useNotification();

  if (!user || !user.id) {
    return null;
  }

  const {
    data: patientReferrals,
    isLoading: isPatientReferralsLoading,
    isError: isPatientReferralsError,
  } = useGetAllReferralsForPatient(user.id, { query: { enabled: isPatient } });

  const {
    data: allReferrals,
    isLoading: isAllLoading,
    isError: isAllError,
  } = useGetAllReferrals({ query: { enabled: !isPatient } });

  const referrals = isPatient ? patientReferrals : allReferrals;
  const isLoading = isPatient ? isPatientReferralsLoading : isAllLoading;
  const isError = isPatientReferralsError || isAllError;

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
  if (referrals) {
    return (
      <div className="flex flex-col">
        <CardHeader title={'Skierowania'} />
        <ReferralsTable referrals={referrals} />
        <NotificationComponent />
      </div>
    );
  }
};

export default ReferralsPage;
