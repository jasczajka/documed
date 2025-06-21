import { yupResolver } from '@hookform/resolvers/yup';
import { Box, Button, TextField } from '@mui/material';
import { FC, useCallback, useEffect } from 'react';
import { Controller, useForm } from 'react-hook-form';
import { useDeactivateAccount } from 'shared/api/generated/auth-controller/auth-controller';
import { PatientDetailsDTO } from 'shared/api/generated/generated.schemas';
import { useUpdatePatientPersonalData } from 'shared/api/generated/patients-controller/patients-controller';
import ConfirmationModal from 'shared/components/ConfirmationModal/ConfirmationModal';
import { useModal } from 'shared/hooks/useModal';
import { useNotification } from 'shared/hooks/useNotification';
import * as Yup from 'yup';

type FormData = {
  firstName: string;
  lastName: string;
  email: string;
  address: string;
  phoneNumber: string;
};

const validationSchema = Yup.object({
  firstName: Yup.string().required('Imię jest wymagane'),
  lastName: Yup.string().required('Nazwisko jest wymagane'),
  phoneNumber: Yup.string()
    .matches(/^\d{9}$/, 'Numer telefonu musi mieć dokładnie 9 cyfr')
    .required('Numer telefonu jest wymagany'),
  email: Yup.string().email('Nieprawidłowy adres email').required('Adres email jest wymagany'),
  address: Yup.string().required('Adres jest wymagany'),
});

interface PersonalDataTabProps {
  patientDetails: PatientDetailsDTO;
  onSuccessfulEdit: () => void;
}

export const PersonalDataTab: FC<PersonalDataTabProps> = ({ patientDetails, onSuccessfulEdit }) => {
  const { showNotification, NotificationComponent } = useNotification();
  // const { isWardClerk } = useAuth();
  const isWardClerk = true;
  const { openModal } = useModal();

  const {
    control,
    handleSubmit,
    formState: { errors },
  } = useForm({
    resolver: yupResolver(validationSchema),
    defaultValues: {
      firstName: patientDetails.firstName,
      lastName: patientDetails.lastName,
      email: patientDetails.email,
      address: patientDetails.address,
      phoneNumber: patientDetails.phoneNumber,
    },
  });

  const {
    mutateAsync: deactivateAccount,
    isPending: isDeactivateAccountLoading,
    isError: isDeactivateAccountError,
  } = useDeactivateAccount();

  const {
    mutateAsync: updatePatientData,
    isPending: isUpdatePatientDataLoading,
    isError: isUpdatePatientDataError,
  } = useUpdatePatientPersonalData();

  const handleConfirmDeactivateAccount = useCallback(async () => {
    await deactivateAccount({ id: patientDetails.id });
    onSuccessfulEdit();
    showNotification('Udało się usunąć konto', 'success');
  }, [patientDetails.id]);

  const handleDeactivateAccountClick = () => {
    openModal('confirmAccountDeactivationModal', (close) => (
      <ConfirmationModal
        onConfirm={() => {
          handleConfirmDeactivateAccount();
          close();
        }}
        onCancel={close}
      />
    ));
  };

  const onSubmit = async (data: FormData) => {
    await updatePatientData({ id: patientDetails.id, data });
    onSuccessfulEdit();
    showNotification('Udało się zaktualizować dane pacjenta', 'success');
  };

  useEffect(() => {
    if (isDeactivateAccountError) {
      showNotification('Nie udało się usunąć danych osobowych pacjenta', 'error');
    }
    if (isUpdatePatientDataError) {
      showNotification('Nie udało się zaktualizować danych pacjenta', 'error');
    }
  }, [isDeactivateAccountError, isUpdatePatientDataError]);

  return (
    <div className="flex w-1/2 flex-col gap-8">
      <Box
        component="form"
        onSubmit={handleSubmit(onSubmit)}
        sx={{ display: 'flex', flexDirection: 'column', gap: 3 }}
      >
        <Controller
          name="firstName"
          control={control}
          render={({ field }) => (
            <TextField
              {...field}
              label="Imię"
              error={!!errors.firstName}
              helperText={errors.firstName?.message}
              fullWidth
              disabled={isUpdatePatientDataLoading}
              slotProps={{ input: { readOnly: !isWardClerk } }}
            />
          )}
        />
        <Controller
          name="lastName"
          control={control}
          render={({ field }) => (
            <TextField
              {...field}
              label="Nazwisko"
              error={!!errors.lastName}
              helperText={errors.lastName?.message}
              fullWidth
              disabled={isUpdatePatientDataLoading}
              slotProps={{ input: { readOnly: !isWardClerk } }}
            />
          )}
        />
        <Controller
          name="email"
          control={control}
          render={({ field }) => (
            <TextField
              {...field}
              label="Email"
              error={!!errors.email}
              helperText={errors.email?.message}
              fullWidth
              disabled={isUpdatePatientDataLoading}
              slotProps={{ input: { readOnly: !isWardClerk } }}
            />
          )}
        />
        <Controller
          name="phoneNumber"
          control={control}
          render={({ field }) => (
            <TextField
              {...field}
              label="Numer telefonu"
              error={!!errors.phoneNumber}
              helperText={errors.phoneNumber?.message}
              fullWidth
              disabled={isUpdatePatientDataLoading}
              slotProps={{ input: { readOnly: !isWardClerk } }}
            />
          )}
        />
        <Controller
          name="address"
          control={control}
          render={({ field }) => (
            <TextField
              {...field}
              label="Adres"
              error={!!errors.address}
              helperText={errors.address?.message}
              fullWidth
              multiline
              minRows={2}
              disabled={isUpdatePatientDataLoading}
              slotProps={{ input: { readOnly: !isWardClerk } }}
            />
          )}
        />
        {isWardClerk && (
          <Box sx={{ display: 'flex', gap: 2 }}>
            <Button
              type="submit"
              variant="contained"
              disabled={isUpdatePatientDataLoading || isDeactivateAccountLoading}
              loading={isUpdatePatientDataLoading}
            >
              Zapisz zmiany
            </Button>
          </Box>
        )}
      </Box>

      {isWardClerk && (
        <Button
          disabled={isDeactivateAccountLoading || isUpdatePatientDataLoading}
          loading={isDeactivateAccountLoading}
          onClick={handleDeactivateAccountClick}
          sx={{ width: 290 }}
          variant="contained"
          color="error"
        >
          Usuń dane osobowe pacjenta
        </Button>
      )}
      <NotificationComponent />
    </div>
  );
};
