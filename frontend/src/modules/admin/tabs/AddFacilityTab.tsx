import { yupResolver } from '@hookform/resolvers/yup';
import { Box, Button, TextField } from '@mui/material';
import { Controller, useForm } from 'react-hook-form';
import { useCreateFacility } from 'shared/api/generated/facility-controller/facility-controller';
import { FullPageLoadingSpinner } from 'shared/components/FullPageLoadingSpinner';
import { useFacilityStore } from 'shared/hooks/stores/useFacilityStore';
import { useNotification } from 'shared/hooks/useNotification';

import * as Yup from 'yup';
type NewFacilityFormData = {
  address: string;
  city: string;
};

const validationSchema = Yup.object({
  address: Yup.string()
    .required('Adres jest wymagany')
    .max(255, 'Adres może mieć maksymalnie 255 znaków'),
  city: Yup.string()
    .required('Miasto jest wymagane')
    .max(255, 'Miasto może mieć maksymalnie 255 znaków'),
});

export const AddFacilityTab = () => {
  const { showNotification, NotificationComponent } = useNotification();
  const fetchFacilities = useFacilityStore((state) => state.fetchFacilities);

  const {
    control,
    handleSubmit,
    formState: { errors },
    reset,
  } = useForm<NewFacilityFormData>({
    resolver: yupResolver(validationSchema),
    defaultValues: {
      address: '',
      city: '',
    },
  });

  const { mutateAsync: createFacility, isPending: isCreateFacilityLoading } = useCreateFacility();

  const handleCreateFacility = async (data: NewFacilityFormData) => {
    try {
      await createFacility({ data: { address: data.address, city: data.city } });
      await fetchFacilities();
      showNotification('Udało się dodać placówkę!', 'success');
      reset();
    } catch (error) {
      showNotification('Błąd podczas dodawania placówki', 'error');
      console.error(error);
    }
  };

  if (isCreateFacilityLoading) {
    return (
      <>
        <FullPageLoadingSpinner />
        <NotificationComponent />
      </>
    );
  }

  return (
    <Box
      component="form"
      onSubmit={handleSubmit(handleCreateFacility)}
      sx={{ mt: 4, display: 'flex', flexDirection: 'column', gap: 2 }}
    >
      <Controller
        name="address"
        control={control}
        render={({ field }) => (
          <TextField
            {...field}
            label="Adres placówki"
            fullWidth
            error={!!errors.address}
            helperText={errors.address?.message}
            multiline
            minRows={2}
          />
        )}
      />
      <Controller
        name="city"
        control={control}
        render={({ field }) => (
          <TextField
            {...field}
            label="Miasto placówki"
            fullWidth
            error={!!errors.city}
            helperText={errors.city?.message}
          />
        )}
      />
      <Box sx={{ display: 'flex', justifyContent: 'flex-end', mt: 2 }}>
        <Button
          type="submit"
          variant="contained"
          disabled={isCreateFacilityLoading}
          loading={isCreateFacilityLoading}
        >
          Dodaj placówkę
        </Button>
      </Box>

      <NotificationComponent />
    </Box>
  );
};
