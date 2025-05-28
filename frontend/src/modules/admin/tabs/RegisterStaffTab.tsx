import { yupResolver } from '@hookform/resolvers/yup';
import {
  Box,
  Button,
  FormControl,
  FormControlLabel,
  Radio,
  RadioGroup,
  TextField,
} from '@mui/material';
import { FC } from 'react';
import { Controller, FormProvider, useForm } from 'react-hook-form';
import { useRegisterStaff } from 'shared/api/generated/auth-controller/auth-controller';
import { MeDTORole } from 'shared/api/generated/generated.schemas';
import { useNotification } from 'shared/hooks/useNotification';
import { mapAuthError } from 'shared/utils/mapAuthError';
import * as Yup from 'yup';

type FormData = {
  role: MeDTORole;
  email: string;
  firstName: string;
  lastName: string;
  password: string;
};

const validationSchema = Yup.object({
  role: Yup.mixed<MeDTORole>()
    .oneOf(Object.values(MeDTORole))
    .required('Typ użytkownika jest wymagany'),
  email: Yup.string().email('Nieprawidłowy adres email').required('Adres email jest wymagany'),
  firstName: Yup.string().required('Imię jest wymagane'),
  lastName: Yup.string().required('Nazwisko jest wymagane'),
  password: Yup.string()
    .min(6, 'Hasło musi mieć co najmniej 6 znaków')
    .required('Hasło jest wymagane'),
});

export const RegisterStaffTab: FC = () => {
  const { showNotification, NotificationComponent } = useNotification();

  const methods = useForm({
    resolver: yupResolver(validationSchema),
    defaultValues: {
      role: MeDTORole.WARD_CLERK,
      email: '',
      firstName: '',
      lastName: '',
      password: '',
    },
  });
  const {
    control,
    handleSubmit,
    formState: { errors },
    reset,
  } = methods;

  const { mutateAsync: registerDoctor, isPending: isLoading } = useRegisterStaff({
    mutation: {
      onSuccess: () => {
        showNotification('Pomyślnie dodano konto pracownika!', 'success');
        reset();
      },
      onError: (error) => {
        const errorResult = mapAuthError(error);
        if (errorResult) {
          showNotification(`Błąd: ${errorResult.message}`, 'error');
        } else {
          showNotification('Wystąpił nieznany błąd', 'error');
        }
        console.error('Error registering doctor:', error);
      },
    },
  });

  const onSubmit = async (data: FormData) => {
    await registerDoctor({
      data: {
        role: data.role,
        email: data.email,
        firstName: data.firstName,
        lastName: data.lastName,
        password: data.password,
      },
    });
  };

  return (
    <FormProvider {...methods}>
      <Box sx={{ display: 'flex', flexDirection: 'column', height: '100%', padding: 4 }}>
        <Controller
          name="role"
          control={control}
          render={({ field }) => (
            <FormControl component="fieldset" error={!!errors.role}>
              <RadioGroup {...field} row>
                <FormControlLabel
                  value={MeDTORole.NURSE}
                  control={<Radio />}
                  label="Pielęgniarka"
                />
                <FormControlLabel
                  value={MeDTORole.WARD_CLERK}
                  control={<Radio color="secondary" />}
                  label="Rejestrator"
                />
                <FormControlLabel
                  value={MeDTORole.ADMINISTRATOR}
                  control={<Radio />}
                  label="Administrator"
                />
              </RadioGroup>
            </FormControl>
          )}
        />

        <Box
          component="form"
          onSubmit={handleSubmit(onSubmit)}
          sx={{
            display: 'flex',
            flexDirection: 'column',
            flexGrow: 1,
            gap: 6,
          }}
        >
          <Box sx={{ display: 'flex', flexDirection: 'column', gap: 6 }}>
            <Controller
              name="email"
              control={control}
              render={({ field }) => (
                <TextField
                  {...field}
                  label="Adres-email"
                  placeholder="Wprowadź adres-email"
                  error={!!errors.email}
                  helperText={errors.email?.message}
                  fullWidth
                />
              )}
            />
            <Controller
              name="firstName"
              control={control}
              render={({ field }) => (
                <TextField
                  {...field}
                  label="Imię"
                  placeholder="Wprowadź imię"
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
                  placeholder="Wprowadź nazwisko"
                  error={!!errors.lastName}
                  helperText={errors.lastName?.message}
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
          </Box>
          <Box sx={{ flexGrow: 1 }} />
          <Box sx={{ alignSelf: 'flex-end', mt: 2 }}>
            <Button variant="contained" type="submit" loading={isLoading} disabled={isLoading}>
              Zarejestruj
            </Button>
          </Box>
        </Box>
        <NotificationComponent />
      </Box>
    </FormProvider>
  );
};
