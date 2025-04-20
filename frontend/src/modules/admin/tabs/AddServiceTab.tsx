import { yupResolver } from '@hookform/resolvers/yup';
import {
  Alert,
  Box,
  Button,
  Chip,
  FormControl,
  FormControlLabel,
  FormHelperText,
  InputLabel,
  MenuItem,
  Select,
  Snackbar,
  Switch,
  TextField,
} from '@mui/material';
import { FC, useState } from 'react';
import { Controller, useForm } from 'react-hook-form';
import { ServiceType, Specialization } from 'shared/api/generated/generated.schemas';
import { useCreateService } from 'shared/api/generated/service-controller/service-controller';
import { useGetAllSpecializations } from 'shared/api/generated/specialization-controller/specialization-controller';
import { appConfig } from 'shared/appConfig';
import * as Yup from 'yup';

type FormData = {
  specializationIds: number[];
  name: string;
  estimatedTime: number;
  price: number;
  type: ServiceType;
};

const validationSchema = Yup.object({
  specializationIds: Yup.array()
    .of(Yup.number().required())
    .min(1, 'Trzeba wybrać przynajmniej jedną specjalizację')
    .required('Trzeba wybrać przynajmniej jedną specjalizację'),
  name: Yup.string().required('Nazwa usługi jest wymagana'),
  estimatedTime: Yup.number()
    .required('Czas trwania jest wymagany')
    .min(1, 'Czas trwania musi być większy od 0'),
  price: Yup.number().required('Cena jest wymagana').min(1, 'Cena musi być większa od 0'),
  type: Yup.mixed<ServiceType>()
    .oneOf(Object.values(ServiceType))
    .required('Typ usługi jest wymagany'),
});

export const AddServiceTab: FC = () => {
  const { data: specializations } = useGetAllSpecializations();
  const getSpecializationOptions = (specializations: Specialization[]) =>
    specializations.map((spec) => ({ id: spec.id, name: spec.name }));

  const [snackbarOpen, setSnackbarOpen] = useState(false);
  const [snackbarMessage, setSnackbarMessage] = useState('');
  const {
    control,
    handleSubmit,
    formState: { errors },
    reset,
  } = useForm({
    resolver: yupResolver(validationSchema),
    defaultValues: {
      specializationIds: [],
      name: '',
      estimatedTime: undefined,
      price: undefined,
      type: ServiceType.REGULAR_SERVICE,
    },
  });

  const { mutateAsync: createService, isPending: isLoading } = useCreateService({
    mutation: {
      onSuccess: () => {
        setSnackbarMessage('Usługa została dodana pomyślnie!');
        setSnackbarOpen(true);
        reset();
      },
      onError: (error) => {
        setSnackbarMessage('Wystąpił błąd podczas dodawania usługi');
        setSnackbarOpen(true);
        console.error('Error creating service:', error);
      },
    },
  });

  const onSubmit = async (data: FormData) => {
    try {
      await createService({ data });
    } catch (error) {
      console.error('Service adding error:', error);
    }
  };

  return (
    <Box
      component="form"
      onSubmit={handleSubmit(onSubmit)}
      sx={{ height: '100%', width: '100%', display: 'flex', gap: 10 }}
    >
      <Box sx={{ flex: 1, display: 'flex', flexDirection: 'column', gap: 2 }}>
        <Controller
          name="type"
          control={control}
          render={({ field }) => (
            <FormControlLabel
              control={
                <Switch
                  checked={field.value === ServiceType.REGULAR_SERVICE}
                  onChange={(e) => {
                    const newValue = e.target.checked
                      ? ServiceType.REGULAR_SERVICE
                      : ServiceType.ADDITIONAL_SERVICE;
                    field.onChange(newValue);
                  }}
                  color="primary"
                />
              }
              label={
                field.value === ServiceType.REGULAR_SERVICE
                  ? 'Standardowa usługa'
                  : 'Usługa dodatkowa'
              }
            />
          )}
        />
        <FormControl error={!!errors.specializationIds}>
          <InputLabel id="specialization-label">Specjalizacje mogące wykonać usługę</InputLabel>
          <Controller
            name="specializationIds"
            control={control}
            render={({ field }) => {
              const options = getSpecializationOptions(specializations ?? []);

              return (
                <Select
                  multiple
                  label="Specjalizacje mogące wykonać usługę"
                  value={field.value}
                  onChange={(e) => field.onChange(e.target.value)}
                  renderValue={(selected) => (
                    <Box sx={{ display: 'flex', flexWrap: 'wrap', gap: 0.5 }}>
                      {selected.map((id) => {
                        const spec = options.find((opt) => opt.id === id);
                        return <Chip key={id} label={spec?.name || id} />;
                      })}
                    </Box>
                  )}
                  sx={{ minWidth: 330, width: '100%' }}
                >
                  {options.map((option) => (
                    <MenuItem key={option.id} value={option.id}>
                      {option.name}
                    </MenuItem>
                  ))}
                </Select>
              );
            }}
          />
          {errors.specializationIds && (
            <FormHelperText>{errors.specializationIds.message}</FormHelperText>
          )}
        </FormControl>
      </Box>
      <Box sx={{ display: 'flex', flexDirection: 'column', gap: 6, width: '100%' }}>
        <Controller
          name="name"
          control={control}
          render={({ field }) => (
            <TextField
              {...field}
              label="Nazwa usługi"
              fullWidth
              error={!!errors.name}
              helperText={errors.name?.message}
            />
          )}
        />
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
              }}
            />
          )}
        />
        <Box sx={{ display: 'flex', justifyContent: 'flex-end', mt: 2 }}>
          <Button
            loading={isLoading}
            disabled={isLoading}
            type="submit"
            variant="contained"
            size="large"
          >
            Dodaj usługę
          </Button>
        </Box>
      </Box>
      <Snackbar
        open={snackbarOpen}
        onClose={() => setSnackbarOpen(false)}
        autoHideDuration={appConfig.snackBarDuration}
        anchorOrigin={{ vertical: 'top', horizontal: 'center' }}
      >
        <Alert severity="success" sx={{ width: '100%' }}>
          {snackbarMessage}
        </Alert>
      </Snackbar>
    </Box>
  );
};
