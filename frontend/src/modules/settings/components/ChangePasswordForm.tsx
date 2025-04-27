import { yupResolver } from '@hookform/resolvers/yup';
import { Box, Button, TextField, Typography } from '@mui/material';
import { FC } from 'react';
import { Controller, useForm } from 'react-hook-form';
import * as Yup from 'yup';
export type FormData = {
  oldPassword: string;
  newPassword: string;
  confirmNewPassword: string;
};

const validationSchema = Yup.object({
  oldPassword: Yup.string()
    .min(6, 'Hasło musi mieć co najmniej 6 znaków')
    .required('Obecne hasło jest wymagane'),
  newPassword: Yup.string()
    .min(6, 'Hasło musi mieć co najmniej 6 znaków')
    .required('Hasło jest wymagane'),
  confirmNewPassword: Yup.string()
    .oneOf([Yup.ref('newPassword')], 'Hasła muszą się zgadzać')
    .required('Wpisz hasło ponownie'),
});

interface ChangePasswordFormProps {
  onSubmit: (data: FormData) => Promise<void>;
  error?: string;
  loading?: boolean;
}

export const ChangePasswordForm: FC<ChangePasswordFormProps> = ({ onSubmit, error, loading }) => {
  const {
    control,
    handleSubmit,
    formState: { errors },
  } = useForm({
    resolver: yupResolver(validationSchema),
    defaultValues: {
      oldPassword: '',
      newPassword: '',
      confirmNewPassword: '',
    },
  });
  return (
    <Box
      component="form"
      onSubmit={handleSubmit(onSubmit)}
      sx={{
        display: 'flex',
        width: '44%',
        flexDirection: 'column',
        gap: 8,
        minWidth: '500px',
      }}
    >
      <Controller
        name="oldPassword"
        control={control}
        render={({ field }) => (
          <TextField
            {...field}
            label="Obecne hasło"
            error={!!errors.oldPassword}
            helperText={errors.oldPassword?.message}
            type="password"
            fullWidth
          />
        )}
      />
      <Controller
        name="newPassword"
        control={control}
        render={({ field }) => (
          <TextField
            {...field}
            label="Nowe hasło"
            error={!!errors.newPassword}
            helperText={errors.newPassword?.message}
            type="password"
            fullWidth
          />
        )}
      />
      <Controller
        name="confirmNewPassword"
        control={control}
        render={({ field }) => (
          <TextField
            {...field}
            label="Wpisz ponownie nowe hasło"
            error={!!errors.confirmNewPassword}
            helperText={errors.confirmNewPassword?.message}
            type="password"
            fullWidth
          />
        )}
      />
      {error && (
        <Typography
          color="error"
          variant="body2"
          sx={{
            alignSelf: 'start',
          }}
        >
          {error}
        </Typography>
      )}
      <Box sx={{ alignSelf: 'flex-end' }}>
        <Button variant="contained" type="submit" loading={loading} disabled={loading}>
          Potwierdź zmianę hasła
        </Button>
      </Box>
    </Box>
  );
};
