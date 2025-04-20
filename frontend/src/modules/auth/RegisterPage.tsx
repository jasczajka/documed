import { yupResolver } from '@hookform/resolvers/yup';
import { Alert, Box, Button, Snackbar, TextField, Typography } from '@mui/material';
import dayjs from 'dayjs';
import { FC, useState } from 'react';
import { Controller, useForm } from 'react-hook-form';
import { Link } from 'react-router';
import { appConfig } from 'shared/appConfig';
import { useAuth } from 'shared/hooks/useAuth';
import { DocuMedLogo } from 'shared/icons/DocuMedLogo';
import * as Yup from 'yup';
import { getBirthDateFromPESEL } from './utils';

type FormData = {
  firstName: string;
  lastName: string;
  pesel: string;
  birthdate: string;
  phoneNumber: string;
  email: string;
  address: string;
  password: string;
  confirmPassword: string;
};

const validationSchema = Yup.object({
  firstName: Yup.string().required('Imię jest wymagane'),
  lastName: Yup.string().required('Nazwisko jest wymagane'),
  birthdate: Yup.string()
    .required('Data urodzenia jest wymagana')
    .matches(/^\d{4}-\d{2}-\d{2}$/, 'Data musi być w formacie RRRR-MM-DD')
    .test(
      'is-adult',
      'Musisz mieć co najmniej 18 lat',
      (value) => dayjs().diff(dayjs(value, 'YYYY-MM-DD'), 'years') >= 18,
    ),
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

export const RegisterPage: FC = () => {
  const [snackbarOpen, setSnackbarOpen] = useState(false);
  const { register, registerError } = useAuth();
  const {
    control,
    handleSubmit,
    formState: { errors },
    setValue,
  } = useForm({
    resolver: yupResolver(validationSchema),
    defaultValues: {
      firstName: '',
      lastName: '',
      pesel: '',
      birthdate: '',
      phoneNumber: '',
      email: '',
      address: '',
      password: '',
      confirmPassword: '',
    },
  });

  const onSubmit = async (data: FormData) => {
    try {
      await register({
        firstName: data.firstName,
        lastName: data.lastName,
        email: data.email,
        pesel: data.pesel,
        password: data.password,
        confirmPassword: data.confirmPassword,
        phoneNumber: data.phoneNumber,
        address: data.address,
        birthdate: data.birthdate,
      });
      setSnackbarOpen(true);
    } catch (error) {
      console.error('Registration error:', error);
    }
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
                onChange={(e) => {
                  field.onChange(e);
                  const value = e.target.value;
                  field.onChange(value);

                  if (value.length === 11) {
                    const date = getBirthDateFromPESEL(value);
                    setValue('birthdate', dayjs(date).format('YYYY-MM-DD'));
                  }
                }}
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
        <Box sx={{ display: 'flex', width: '100%', gap: 6 }}>
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
            name="birthdate"
            control={control}
            render={({ field }) => (
              <TextField
                {...field}
                label="Data urodzenia"
                type="date"
                slotProps={{
                  inputLabel: { shrink: true },
                  htmlInput: { max: dayjs().subtract(18, 'years').format('YYYY-MM-DD') },
                }}
                error={!!errors.birthdate}
                helperText={errors.birthdate?.message}
                fullWidth
              />
            )}
          />
        </Box>

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

        {registerError && (
          <Typography
            color="error"
            variant="body2"
            sx={{
              alignSelf: 'center',
            }}
          >
            {registerError.message}
          </Typography>
        )}

        <Box sx={{ display: 'flex', justifyContent: 'center', paddingTop: 3 }}>
          <Typography variant="body1">Mam już konto.&nbsp;</Typography>
          <Link to="/login">
            <Typography color="primary" variant="body1">
              Zaloguj mnie!
            </Typography>
          </Link>
        </Box>
        <Snackbar
          open={snackbarOpen}
          onClose={() => setSnackbarOpen(false)}
          autoHideDuration={appConfig.snackBarDuration}
          anchorOrigin={{ vertical: 'top', horizontal: 'center' }}
        >
          <Alert severity="success" sx={{ width: '100%' }}>
            Użytkownik zarejestrowany pomyślnie.{' '}
            <Link to="/login" style={{ color: 'inherit', textDecoration: 'underline' }}>
              Kliknij tutaj, aby się zalogować
            </Link>
          </Alert>
        </Snackbar>
      </Box>
    </main>
  );
};

export default RegisterPage;
