import { yupResolver } from '@hookform/resolvers/yup';
import { Box, Button, TextField, Typography } from '@mui/material';
import { FC } from 'react';
import { Controller, useForm } from 'react-hook-form';
import { Link } from 'react-router';
import { DocuMedLogo } from 'shared/icons/DocuMedLogo';
import * as Yup from 'yup';

const validationSchema = Yup.object({
  firstName: Yup.string().required('Imię jest wymagane'),
  lastName: Yup.string().required('Nazwisko jest wymagane'),
  pesel: Yup.string()
    .matches(/^\d{11}$/, 'PESEL musi mieć dokładnie 11 cyfr')
    .required('PESEL jest wymagany'),
  phoneNumber: Yup.string()
    .matches(/^\d{9}$/, 'Numer telefonu musi mieć dokładnie 9 cyfr')
    .required('Numer telefonu jest wymagany'),
  email: Yup.string().email('Nieprawidłowy adres email').required('Adres email jest wymagany'),
  address: Yup.string().required('Adres jest wymagany'),
  password: Yup.string()
    .min(6, 'Hasło musi mieć co najmniej 6 znaków')
    .required('Hasło jest wymagane'),
  confirmPassword: Yup.string()
    .oneOf([Yup.ref('password')], 'Hasła muszą się zgadzać')
    .required('Wpisz hasło ponownie'),
});

export const Register: FC = () => {
  const {
    control,
    handleSubmit,
    formState: { errors },
  } = useForm({
    resolver: yupResolver(validationSchema),
    defaultValues: {
      firstName: '',
      lastName: '',
      pesel: '',
      phoneNumber: '',
      email: '',
      address: '',
      password: '',
      confirmPassword: '',
    },
  });

  const onSubmit = (data: any) => {
    console.log('Form data:', data);
  };

  return (
    <main className="flex h-full w-full flex-col items-center justify-center">
      <DocuMedLogo className="text-primary w-[170px] pt-2 pb-10" />
      <Typography variant="h3">Utwórz konto</Typography>
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
        <Box sx={{ display: 'flex', width: '100%', gap: 6 }}>
          <Controller
            name="firstName"
            control={control}
            render={({ field }) => (
              <TextField
                {...field}
                label="Imię"
                placeholder="Wprowadź swoje imię"
                error={!!errors.firstName}
                helperText={errors.firstName?.message}
                fullWidth
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
                placeholder="Wprowadź swoje nazwisko"
                error={!!errors.lastName}
                helperText={errors.lastName?.message}
                fullWidth
              />
            )}
          />
        </Box>

        <Box sx={{ display: 'flex', width: '100%', gap: 6 }}>
          <Controller
            name="pesel"
            control={control}
            render={({ field }) => (
              <TextField
                {...field}
                label="PESEL"
                placeholder="Wprowadź swój PESEL"
                error={!!errors.pesel}
                helperText={errors.pesel?.message}
                fullWidth
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
                placeholder="Wprowadź swój numer telefonu"
                error={!!errors.phoneNumber}
                helperText={errors.phoneNumber?.message}
                fullWidth
              />
            )}
          />
        </Box>

        <Controller
          name="email"
          control={control}
          render={({ field }) => (
            <TextField
              {...field}
              label="Adres-email"
              placeholder="Wprowadź swój adres-email"
              error={!!errors.email}
              helperText={errors.email?.message}
              fullWidth
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
              placeholder="Wprowadź swój adres"
              error={!!errors.address}
              helperText={errors.address?.message}
              fullWidth
              multiline
              minRows={2}
            />
          )}
        />

        <Box sx={{ display: 'flex', width: '100%', gap: 6 }}>
          <Controller
            name="password"
            control={control}
            render={({ field }) => (
              <TextField
                {...field}
                label="Hasło"
                placeholder="Wprowadź swoje hasło"
                error={!!errors.password}
                helperText={errors.password?.message}
                type="password"
                fullWidth
              />
            )}
          />
          <Controller
            name="confirmPassword"
            control={control}
            render={({ field }) => (
              <TextField
                {...field}
                label="Powtórz hasło"
                placeholder="Powtórz hasło"
                error={!!errors.confirmPassword}
                helperText={errors.confirmPassword?.message}
                type="password"
                fullWidth
              />
            )}
          />
        </Box>

        <Button variant="contained" type="submit">
          Zarejestruj się
        </Button>

        <Box sx={{ display: 'flex', justifyContent: 'center', paddingTop: 3 }}>
          <Typography variant="body1">Mam już konto.&nbsp;</Typography>
          <Link to="/login">
            <Typography color="primary" variant="body1">
              Zaloguj mnie!
            </Typography>
          </Link>
        </Box>
      </Box>
    </main>
  );
};
