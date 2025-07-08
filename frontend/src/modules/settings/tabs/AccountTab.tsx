import { Box, TextField } from '@mui/material';
import { useChangePassword } from 'shared/api/generated/auth-controller/auth-controller';
import { ChangePasswordRequestDTO } from 'shared/api/generated/generated.schemas';
import { useGetPatientDetails } from 'shared/api/generated/patients-controller/patients-controller';
import { SubscriptionServicesTable } from 'shared/components/SubscriptionServicesTable/SubscriptionServicesTable';
import { useServicesStore } from 'shared/hooks/stores/useServicesStore';
import { useSubscriptionStore } from 'shared/hooks/stores/useSubscriptionStore';
import { useAuth } from 'shared/hooks/useAuth';
import { useNotification } from 'shared/hooks/useNotification';
import { mapAuthError } from 'shared/utils/mapAuthError';
import { ChangePasswordForm } from '../components/ChangePasswordForm';

export const AccountTab = () => {
  const { user, isPatient } = useAuth();
  const { data: patientInfo } = useGetPatientDetails(user!.id, { query: { enabled: isPatient } });

  const subscriptions = useSubscriptionStore((state) => state.subscriptions);
  const allServices = [
    ...useServicesStore((state) => state.regularServices),
    ...useServicesStore((state) => state.addditionalServices),
  ];
  const subscription = subscriptions.find((sub) => sub.id === patientInfo?.subscriptionId);

  const {
    mutateAsync: changePassword,
    error: changePasswordError,
    isPending: isLoading,
  } = useChangePassword({
    mutation: {
      onError: () => {
        showNotification('Coś poszło nie tak, czy na pewno podałaś/eś prawidłowe hasło?', 'error');
      },
    },
  });
  const { showNotification, NotificationComponent } = useNotification();

  const handleChangePasswordSubmit = async (data: ChangePasswordRequestDTO) => {
    await changePassword({ data });
    showNotification('Hasło zostało zmienione!', 'success');
  };

  return (
    <Box sx={{ display: 'flex', flexDirection: 'row', gap: 12, width: '100%' }}>
      <ChangePasswordForm
        loading={isLoading}
        error={mapAuthError(changePasswordError)?.message}
        onSubmit={handleChangePasswordSubmit}
      />

      {isPatient && (
        <Box sx={{ display: 'flex', flexDirection: 'column', gap: 4, width: '100%' }}>
          <TextField
            sx={{ pointerEvents: 'none' }}
            slotProps={{ input: { readOnly: true } }}
            label="Subsrykpcja"
            value={subscription ? subscription.name : 'Brak subskrypcji'}
          />
          {subscription && (
            <Box>
              <SubscriptionServicesTable
                allServices={allServices}
                subscriptionId={subscription.id}
              />
            </Box>
          )}
        </Box>
      )}
      <NotificationComponent />
    </Box>
  );
};
