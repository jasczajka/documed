import { useChangePassword } from 'shared/api/generated/auth-controller/auth-controller';
import { ChangePasswordRequestDTO } from 'shared/api/generated/generated.schemas';
import { useNotification } from 'shared/hooks/useNotification';
import { mapAuthError } from 'shared/utils/mapAuthError';
import { ChangePasswordForm } from '../components/ChangePasswordForm';

export const AccountTab = () => {
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
    <>
      <ChangePasswordForm
        loading={isLoading}
        error={mapAuthError(changePasswordError)?.message}
        onSubmit={handleChangePasswordSubmit}
      />
      <NotificationComponent />
    </>
  );
};
