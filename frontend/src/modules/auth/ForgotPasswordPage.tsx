import { yupResolver } from '@hookform/resolvers/yup';
import { Box, Button, TextField } from '@mui/material';
import { FC, useState } from 'react';
import { Controller, useForm } from 'react-hook-form';
import {
  useRequestPasswordReset,
  useResetPassword,
} from 'shared/api/generated/auth-controller/auth-controller';
import { useNotification } from 'shared/hooks/useNotification';
import { DocuMedLogo } from 'shared/icons/DocuMedLogo';
import { mapAuthError } from 'shared/utils/mapAuthError';
import * as Yup from 'yup';
import { OTPInput } from './components/OtpInput';

type ForgotPasswordData = {
  email: string;
};

const otpSchema = Yup.object({
  email: Yup.string().email('Nieprawidłowy adres email').required('Adres email jest wymagany'),
});

export const ForgotPasswordPage: FC = () => {
  const { control, handleSubmit } = useForm<ForgotPasswordData>({
    resolver: yupResolver(otpSchema),
  });
  const { showNotification, NotificationComponent } = useNotification();
  const [isOtpSent, setIsOtpSent] = useState(false);
  const [submittedEmail, setSubmittedEmail] = useState<string | null>(null);

  const {
    mutateAsync: requestPasswordReset,
    isPending: isRequestPasswordResetLoading,
    error: requestPasswordResetError,
  } = useRequestPasswordReset();
  const {
    mutateAsync: confirmPasswordReset,
    isPending: isConfirmPasswordResetLoading,
    error: confirmPasswordResetError,
  } = useResetPassword();

  const onSubmit = async (data: ForgotPasswordData) => {
    await requestPasswordReset({ data });
    setIsOtpSent(true);
    setSubmittedEmail(data.email);
  };

  const onOtpSubmit = async (otp: string) => {
    if (!submittedEmail) {
      return;
    }
    await confirmPasswordReset({
      data: {
        email: submittedEmail,
        otp,
      },
    });
    showNotification(`Nowe hasło zostało wysłane na e-mail ${submittedEmail}`, 'success');
  };

  return (
    <main className="flex h-full w-full flex-col items-center justify-center pt-40">
      <DocuMedLogo className="text-primary w-[170px] pt-2 pb-10" />
      {isOtpSent ? (
        <>
          <OTPInput
            onSubmit={onOtpSubmit}
            loading={isConfirmPasswordResetLoading}
            error={mapAuthError(confirmPasswordResetError)?.message}
          />
          <NotificationComponent />
        </>
      ) : (
        <Box
          component="form"
          onSubmit={handleSubmit(onSubmit)}
          sx={{
            display: 'flex',
            width: '44%',
            flexDirection: 'column',
            gap: 8,
            paddingTop: 6,
            minWidth: '500px',
          }}
        >
          <Controller
            name="email"
            control={control}
            render={({ field, fieldState }) => (
              <TextField
                {...field}
                label="Email"
                placeholder="Wprowadź e-mail swojego konta"
                error={!!fieldState.error || !!requestPasswordResetError}
                helperText={
                  fieldState.error?.message || mapAuthError(requestPasswordResetError)?.message
                }
                fullWidth
              />
            )}
          />
          <Button
            variant="contained"
            type="submit"
            loading={isRequestPasswordResetLoading}
            disabled={isRequestPasswordResetLoading}
          >
            Wyślij kod do resetowania hasła
          </Button>
        </Box>
      )}
    </main>
  );
};

export default ForgotPasswordPage;
