import { yupResolver } from '@hookform/resolvers/yup';
import { Box, Button, FormControlLabel, Switch, TextField } from '@mui/material';
import { FC } from 'react';
import { Controller, FormProvider, useForm } from 'react-hook-form';
import { ServiceType } from 'shared/api/generated/generated.schemas';
import { useCreateService } from 'shared/api/generated/service-controller/service-controller';
import { SpecializationSelect } from 'shared/components/SpecializationSelect';
import { useSpecializationsStore } from 'shared/hooks/stores/useSpecializationsStore';
import { useNotification } from 'shared/hooks/useNotification';
import { mapApiError } from 'shared/utils/mapApiError';
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
  const specializations = useSpecializationsStore((state) => state.specializations);
  const { showNotification, NotificationComponent } = useNotification();

  const methods = useForm({
    resolver: yupResolver(validationSchema),
    defaultValues: {
      specializationIds: [],
      name: '',
      estimatedTime: undefined,
      price: undefined,
      type: ServiceType.REGULAR_SERVICE,
    },
  });

  const {
    control,
    handleSubmit,
    formState: { errors },
    reset,
  } = methods;

  const { mutateAsync: createService, isPending: isLoading } = useCreateService({
    mutation: {
      onSuccess: () => {
        showNotification('Usługa została dodana pomyślnie!', 'success');
        reset();
      },
      onError: (error) => {
        const errorResult = mapApiError(error);
        if (errorResult) {
          showNotification(`Błąd: ${errorResult.message}`, 'error');
        } else {
          showNotification('Wystąpił nieznany błąd', 'error');
        }
        console.error('Error creating service:', error);
      },
    },
  });

  const onSubmit = async (data: FormData) => {
    try {
      console.log(data);
      await createService({ data });
    } catch (error) {
      console.error('Service adding error:', error);
    }
  };

  return (
    <FormProvider {...methods}>
      <Box
        component="form"
        onSubmit={handleSubmit(onSubmit)}
        sx={{
          height: '100%',
          width: '100%',
          display: 'flex',
          flexDirection: 'column',
          justifyContent: 'space-between',
          gap: 10,
        }}
      >
        <Box
          sx={{
            height: '100%',
            width: '100%',
            display: 'flex',
            gap: 10,
          }}
        >
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
        </Box>
        <Box
          sx={{
            display: 'flex',
            flexDirection: 'column',
            gap: 6,
            height: '100%',
            width: '70%',
          }}
        >
          <SpecializationSelect
            label="Specjalizacje mogące wykonywać usługę"
            specializations={specializations ?? []}
          />
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
        </Box>

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
      <NotificationComponent />
    </FormProvider>
  );
};
