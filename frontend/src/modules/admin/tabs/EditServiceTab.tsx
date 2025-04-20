import { yupResolver } from '@hookform/resolvers/yup';
import {
  Alert,
  Box,
  Button,
  FormControl,
  InputLabel,
  MenuItem,
  Select,
  Snackbar,
  TextField,
} from '@mui/material';
import { useEffect, useState } from 'react';
import { Controller, useForm } from 'react-hook-form';
import { Service } from 'shared/api/generated/generated.schemas';
import {
  useGetAllServices,
  useUpdateServicePrice,
  useUpdateServiceTime,
} from 'shared/api/generated/service-controller/service-controller';
import { appConfig } from 'shared/appConfig';
import { FullPageLoadingSpinner } from 'shared/components/FileUpload/FullPageLoadingSpinner';
import * as Yup from 'yup';

type FormData = {
  estimatedTime: number;
  price: number;
};

const validationSchema = Yup.object({
  estimatedTime: Yup.number()
    .required('Czas trwania jest wymagany')
    .min(1, 'Czas trwania musi być większy od 0'),
  price: Yup.number().required('Cena jest wymagana').min(1, 'Cena musi być większa od 0'),
});

export const EditServiceTab = () => {
  const { data: services, isLoading: isServicesLoading, refetch } = useGetAllServices();
  const [selectedService, setSelectedService] = useState<Service | null>(null);

  const [snackbarOpen, setSnackbarOpen] = useState(false);
  const [snackbarMessage, setSnackbarMessage] = useState('');

  const {
    control,
    handleSubmit,
    formState: { errors },
    reset,
  } = useForm({
    resolver: yupResolver(validationSchema),
  });

  const { mutateAsync: updatePrice, isPending: isUpdatePriceLoading } = useUpdateServicePrice();
  const { mutateAsync: updateTime, isPending: isUpdateTimeLoading } = useUpdateServiceTime();

  const onSubmit = async (data: FormData) => {
    if (!selectedService) return;
    try {
      await Promise.all([
        updateTime({
          id: selectedService.id,
          data: data.estimatedTime,
        }),
        updatePrice({
          id: selectedService.id,
          data: data.price,
        }),
      ]);
      await refetch();
      setSnackbarMessage('Usługa została zaktualizowana pomyślnie!');
      setSnackbarOpen(true);
    } catch (error) {
      setSnackbarMessage('Wystąpił błąd podczas aktualizowania usługi');
      setSnackbarOpen(true);
      console.error('Service update error:', error);
    }
  };

  const isLoading = isUpdatePriceLoading || isUpdateTimeLoading;

  useEffect(() => {
    if (selectedService) {
      reset({
        estimatedTime: selectedService.estimatedTime,
        price: selectedService.price,
      });
    }
  }, [selectedService, reset]);

  if (!services || isServicesLoading) {
    return <FullPageLoadingSpinner />;
  }

  return (
    <Box
      component="form"
      onSubmit={handleSubmit(onSubmit)}
      sx={{
        height: '100%',
        width: '100%',
        display: 'flex',
        flexDirection: 'column',
        gap: 10,
        justifyContent: 'space-between',
      }}
    >
      <Box sx={{ display: 'flex', flexDirection: 'column', gap: 6, width: '100%', height: '100%' }}>
        <FormControl fullWidth>
          <InputLabel>Wybierz usługę</InputLabel>
          <Select
            value={selectedService?.id || ''}
            onChange={(e) => {
              const service = services.find((s) => s.id === Number(e.target.value));
              setSelectedService(service || null);
            }}
            label="Wybierz usługę"
          >
            {services.map((service) => (
              <MenuItem key={service.id} value={service.id}>
                {service.name} ({service.price} zł, {service.estimatedTime} min)
              </MenuItem>
            ))}
          </Select>
        </FormControl>
        <Controller
          name="estimatedTime"
          control={control}
          render={({ field }) => (
            <TextField
              {...field}
              label="Czas trwania (minuty)"
              type="number"
              fullWidth
              error={!!errors.estimatedTime}
              helperText={errors.estimatedTime?.message}
              onChange={(e) => field.onChange(parseInt(e.target.value) || undefined)}
              slotProps={{
                input: {
                  endAdornment: 'min.',
                },
                inputLabel: {
                  shrink: true,
                },
              }}
            />
          )}
        />
        <Controller
          name="price"
          control={control}
          render={({ field }) => (
            <TextField
              {...field}
              label="Cena (PLN)"
              type="number"
              fullWidth
              error={!!errors.price}
              helperText={errors.price?.message}
              onChange={(e) => field.onChange(parseFloat(e.target.value) || undefined)}
              slotProps={{
                input: {
                  endAdornment: 'zł',
                },
                inputLabel: {
                  shrink: true,
                },
              }}
            />
          )}
        />
      </Box>

      <Snackbar
        open={snackbarOpen}
        autoHideDuration={appConfig.snackBarDuration}
        onClose={() => setSnackbarOpen(false)}
        anchorOrigin={{ vertical: 'top', horizontal: 'center' }}
      >
        <Alert severity="success" sx={{ width: '100%' }}>
          {snackbarMessage}
        </Alert>
      </Snackbar>
      <Box sx={{ display: 'flex', justifyContent: 'flex-end', mt: 2 }}>
        <Button
          loading={isLoading}
          disabled={isLoading}
          type="submit"
          variant="contained"
          size="large"
        >
          Zaktualizuj usługę
        </Button>
      </Box>
    </Box>
  );
};
