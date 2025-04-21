import { yupResolver } from '@hookform/resolvers/yup';
import { Box, Button, TextField } from '@mui/material';
import { FC } from 'react';
import { Controller, FormProvider, useForm } from 'react-hook-form';
import { useRegisterDoctor } from 'shared/api/generated/auth-controller/auth-controller';
import { useGetAllSpecializations } from 'shared/api/generated/specialization-controller/specialization-controller';
import { FullPageLoadingSpinner } from 'shared/components/FileUpload/FullPageLoadingSpinner';
import { SpecializationSelect } from 'shared/components/SpecializationSelect';
import { useNotification } from 'shared/hooks/useNotification';
import { mapAuthError } from 'shared/utils/mapAuthError';
import * as Yup from 'yup';

type FormData = {
  email: string;
  firstName: string;
  lastName: string;
  password: string;
  pwz: string;
  phoneNumber: string;
  specializationIds: number[];
};

const validationSchema = Yup.object({
  email: Yup.string().email('Nieprawidłowy adres email').required('Adres email jest wymagany'),
  firstName: Yup.string().required('Imię jest wymagane'),
  lastName: Yup.string().required('Nazwisko jest wymagane'),
  password: Yup.string()
    .min(6, 'Hasło musi mieć co najmniej 6 znaków')
    .required('Hasło jest wymagane'),
  pwz: Yup.string()
    .matches(/^\d{7}$/, 'Numer PWZ musi mieć dokładnie 7 cyfr')
    .required('PWZ jest wymagany'),
  phoneNumber: Yup.string()
    .matches(/^\d{9}$/, 'Numer telefonu musi mieć dokładnie 9 cyfr')
    .required('Numer telefonu jest wymagany'),
  specializationIds: Yup.array()
    .of(Yup.number().required())
    .min(1, 'Trzeba wybrać przynajmniej jedną specjalizację')
    .required('Trzeba wybrać przynajmniej jedną specjalizację'),
});

export const RegisterDoctorTab: FC = () => {
  const { showNotification, NotificationComponent } = useNotification();
  const { data: specializations, isLoading: isSpecializationsLoading } = useGetAllSpecializations();

  const methods = useForm({
    resolver: yupResolver(validationSchema),
    defaultValues: {
      email: '',
      firstName: '',
      lastName: '',
      password: '',
      pwz: '',
      phoneNumber: '',
      specializationIds: [],
    },
  });
  const {
    control,
    handleSubmit,
    formState: { errors },
    reset,
  } = methods;

  const { mutateAsync: registerDoctor, isPending: isLoading } = useRegisterDoctor({
    mutation: {
      onSuccess: () => {
        showNotification('Pomyślnie dodano konto lekarza!', 'success');
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
    console.log(data);
    await registerDoctor({
      data: {
        email: data.email,
        firstName: data.firstName,
        lastName: data.lastName,
        pwz: data.pwz,
        password: data.password,
        phoneNumber: data.phoneNumber,
        specializationIds: data.specializationIds,
      },
    });
  };

  if (!specializations || isSpecializationsLoading) {
    return <FullPageLoadingSpinner />;
  }

  return (
    <FormProvider {...methods}>
      <Box
        component="form"
        onSubmit={handleSubmit(onSubmit)}
        sx={{ display: 'flex', flexDirection: 'row', width: '100%', gap: 8, padding: 4 }}
      >
        <Box sx={{ display: 'flex', flexDirection: 'column', width: '100%', gap: 6 }}>
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
          <Controller
            name="pwz"
            control={control}
            render={({ field }) => (
              <TextField
                {...field}
                label="PWZ"
                placeholder="Wprowadź nr. PWZ"
                error={!!errors.pwz}
                helperText={errors.pwz?.message}
                fullWidth
                onChange={(e) => {
                  field.onChange(e);
                  const value = e.target.value;
                  field.onChange(value);
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
                placeholder="Wprowadź numer telefonu"
                error={!!errors.phoneNumber}
                helperText={errors.phoneNumber?.message}
                fullWidth
              />
            )}
          />
          <NotificationComponent />
        </Box>
        <SpecializationSelect
          specializations={specializations ?? []}
          label="Specjalizacje lekarza"
        />
        <Box sx={{ alignSelf: 'end' }}>
          <Button variant="contained" type="submit" loading={isLoading} disabled={isLoading}>
            Zarejestruj
          </Button>
        </Box>
      </Box>
    </FormProvider>
  );
};
