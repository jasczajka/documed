import { yupResolver } from '@hookform/resolvers/yup';
import { Box, Button, TextField } from '@mui/material';
import { FC } from 'react';
import { Controller, useForm } from 'react-hook-form';
import * as Yup from 'yup';

type OTPData = {
  otp: string;
};

const otpSchema = Yup.object({
  otp: Yup.string()
    .required('Kod OTP jest wymagany')
    .matches(/^\d{6}$/, 'Kod OTP musi składać się z 6 cyfr'),
});

interface OTPInputProps {
  email: string;
  onSubmit: (otp: string) => Promise<void>;
  error?: string;
  loading?: boolean;
}

export const OTPInput: FC<OTPInputProps> = ({ onSubmit, error, loading }) => {
  const { control, handleSubmit } = useForm<OTPData>({
    resolver: yupResolver(otpSchema),
  });

  return (
    <Box
      component="form"
      onSubmit={handleSubmit((data) => onSubmit(data.otp))}
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
        name="otp"
        control={control}
        render={({ field, fieldState }) => (
          <TextField
            {...field}
            label="Kod OTP"
            placeholder="Wprowadź 6-cyfrowy kod"
            error={!!fieldState.error || !!error}
            helperText={error || fieldState.error?.message}
            fullWidth
            slotProps={{ htmlInput: { maxLength: 6 } }}
          />
        )}
      />

      <Button variant="contained" type="submit" loading={loading} disabled={loading}>
        Zweryfikuj kod
      </Button>
    </Box>
  );
};
