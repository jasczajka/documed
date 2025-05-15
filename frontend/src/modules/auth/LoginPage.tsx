import { yupResolver } from '@hookform/resolvers/yup';
import { Autocomplete, Box, Button, TextField, Typography } from '@mui/material';
import { FC, useEffect, useState } from 'react';
import { Controller, useForm } from 'react-hook-form';
import { Link } from 'react-router';
import { getAllFacilities } from 'shared/api/generated/facility-controller/facility-controller';
import { FacilityLoginReturnDTO } from 'shared/api/generated/generated.schemas';
import { FullPageLoadingSpinner } from 'shared/components/FileUpload/FullPageLoadingSpinner';
import { useAuth } from 'shared/hooks/useAuth';
import { useSitemap } from 'shared/hooks/useSitemap';
import { DocuMedLogo } from 'shared/icons/DocuMedLogo';
import * as Yup from 'yup';

type FormData = {
  login: string;
  password: string;
  facilityId: number;
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
  facilityId: Yup.number().required('Proszę wybrać placówkę'),
});

export const LoginPage: FC = () => {
  const [facilities, setFacilities] = useState<FacilityLoginReturnDTO[] | null>(null);
  const { login, loading, loginError } = useAuth();

  useEffect(() => {
    getAllFacilities().then((returnedFacilities) => setFacilities(returnedFacilities));
  }, []);

  const {
    control,
    handleSubmit,
    formState: { errors },
  } = useForm({
    resolver: yupResolver(validationSchema),
    defaultValues: {
      login: '',
      password: '',
      facilityId: undefined,
    },
  });
  const sitemap = useSitemap();
  console.log('sitemap: ', sitemap);

  const onSubmit = async (data: FormData) => {
    try {
      await login(data);
    } catch (error) {
      console.error('Login error:', error);
    }
  };

  if (!facilities) {
    return <FullPageLoadingSpinner />;
  }

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
        <Controller
          name="facilityId"
          control={control}
          render={({ field }) => (
            <Autocomplete
              options={facilities}
              getOptionLabel={(option) => `${option.city} ${option.address}`}
              onChange={(_, value) => field.onChange(value?.id)}
              value={facilities.find((f) => f.id === field.value)}
              renderInput={(params) => (
                <TextField
                  {...params}
                  label="Placówka"
                  error={!!errors.facilityId}
                  helperText={errors.facilityId?.message}
                />
              )}
              fullWidth
            />
          )}
        />

        <Button variant="contained" type="submit" disabled={loading} loading={loading}>
          Zaloguj się
        </Button>
        {loginError && (
          <Typography
            color="error"
            variant="body2"
            sx={{
              alignSelf: 'center',
            }}
          >
            {loginError.message}
          </Typography>
        )}
        <Box sx={{ display: 'flex', justifyContent: 'space-between', paddingTop: 3 }}>
          <Link to={sitemap.forgotPassword}>
            <Typography color="primary" variant="body1">
              Zapomniałem hasła!
            </Typography>
          </Link>
          <Box sx={{ display: 'flex', paddingTop: 1 }}>
            <Typography variant="body1">Nie masz konta?&nbsp;</Typography>
            <Link to={sitemap.register}>
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
