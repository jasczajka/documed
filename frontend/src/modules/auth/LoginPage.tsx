import { yupResolver } from '@hookform/resolvers/yup';
import { Box, Button, TextField, Typography } from '@mui/material';
import { FC } from 'react';
import { Controller, useForm } from 'react-hook-form';
import { Link, useNavigate } from 'react-router';
import { useAuth } from 'shared/hooks/useAuth';
import { DocuMedLogo } from 'shared/icons/DocuMedLogo';
import * as Yup from 'yup';

type FormData = {
  login: string;
  password: string;
};

const validationSchema = Yup.object({
  login: Yup.string()
    .required('To pole jest wymagane')
    .test(
      'email-or-phone',
      'Musi być poprawnym adresem e-mail lub dokładnie 11 cyframi',
      (value) => Yup.string().email().isValidSync(value) || /^[0-9]{11}$/.test(value),
    ),
  password: Yup.string().required('Hasło jest wymagane'),
});

export const LoginPage: FC = () => {
  const navigate = useNavigate();
  const { login } = useAuth();
  const {
    control,
    handleSubmit,
    formState: { errors },
  } = useForm({
    resolver: yupResolver(validationSchema),
    defaultValues: {
      login: '',
      password: '',
    },
  });

  const onSubmit = async (data: FormData) => {
    try {
      await login(data);
      navigate('/');
    } catch (error) {
      console.error('Login error:', error);
    }
  };

  return (
    <main className="flex h-full w-full flex-col items-center justify-center pt-40">
      <DocuMedLogo className="text-primary w-[170px] pt-2 pb-10" />
      <Typography variant="h3">Zaloguj się</Typography>
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
          name="login"
          control={control}
          render={({ field }) => (
            <TextField
              {...field}
              label="Adres e-mail lub PESEL"
              placeholder="Wprowadź swój adres e-mail lub PESEL"
              error={!!errors.login}
              helperText={errors.login?.message}
              fullWidth
            />
          )}
        />
        <Controller
          name="password"
          control={control}
          render={({ field }) => (
            <TextField
              {...field}
              label="Hasło"
              placeholder="Wprowadź hasło"
              error={!!errors.password}
              helperText={errors.password?.message}
              type="password"
              fullWidth
            />
          )}
        />

        <Button variant="contained" type="submit">
          Zaloguj się
        </Button>
        <Box sx={{ display: 'flex', justifyContent: 'space-between', paddingTop: 3 }}>
          {/* @TODO when Email Module is ready, implement forgot password functionality with sending a new password
          <Button variant="outlined" sx={{ width: '50%' }}>
            Zapomniałem hasła
          </Button> */}
          <Box sx={{ display: 'flex', paddingTop: 1 }}>
            <Typography variant="body1">Nie masz konta?&nbsp;</Typography>
            <Link to="/register">
              <Typography color="primary" variant="body1">
                Zarejestruj się!
              </Typography>
            </Link>
          </Box>
        </Box>
      </Box>
    </main>
  );
};

export default LoginPage;
