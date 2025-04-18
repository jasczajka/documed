import { CardHeader } from '@mui/material';
import { PrescriptionsTable } from 'modules/prescriptions/PrescriptionsTable/PrescriptionsTable';
import { FC } from 'react';
import {
  useGetAllPrescriptions,
  useGetPrescriptionsForUser,
} from 'shared/api/generated/prescription-controller/prescription-controller';
import { FullPageLoadingSpinner } from 'shared/components/FileUpload/FullPageLoadingSpinner';
import { useAuth } from 'shared/hooks/useAuth';

const PrescriptionsPage: FC = () => {
  const { user, isPatient } = useAuth();

  if (!user || !user.id) {
    return null;
  }

  const {
    data: patientPrescriptions,
    isLoading: isPatientLoading,
    isError: isPatientError,
    error: patientError,
  } = useGetPrescriptionsForUser(user.id, { query: { enabled: isPatient } });

  const {
    data: allPrescriptions,
    isLoading: isAllLoading,
    isError: isAllError,
    error: allError,
  } = useGetAllPrescriptions({ query: { enabled: !isPatient } });

  const prescriptions = isPatient ? patientPrescriptions : allPrescriptions;
  const isLoading = isPatient ? isPatientLoading : isAllLoading;
  const isError = isPatient ? isPatientError : isAllError;
  const error = isPatient ? patientError : allError;

  if (isLoading) {
    return <FullPageLoadingSpinner />;
  }

  if (isError) {
    return <div>Error placeholder: {error?.message}</div>;
  }
  if (prescriptions) {
    return (
      <div className="flex flex-col pb-10">
        <CardHeader title={'Recepty'} />
        <PrescriptionsTable prescriptions={prescriptions} />
      </div>
    );
  }
};

export default PrescriptionsPage;
