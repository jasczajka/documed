import { yupResolver } from '@hookform/resolvers/yup';
import { Box, Button, MenuItem, Select, TextField, Typography } from '@mui/material';
import { useEffect, useState } from 'react';
import { Controller, useForm } from 'react-hook-form';
import { useGetAllServices } from 'shared/api/generated/service-controller/service-controller';
import {
  updateServiceDiscount,
  useCreateSubscription,
  useGetAllServiceSubscriptionDiscounts,
  useGetAllSubscriptions,
} from 'shared/api/generated/subscription-controller/subscription-controller';
import { FullPageLoadingSpinner } from 'shared/components/FullPageLoadingSpinner';
import { SubscriptionServicesTable } from 'shared/components/SubscriptionServicesTable/SubscriptionServicesTable';
import { useNotification } from 'shared/hooks/useNotification';

import * as Yup from 'yup';
type NewSubscriptionFormData = {
  name: string;
  price: number;
};

const validationSchema = Yup.object({
  name: Yup.string()
    .required('Nazwa subskrypcji jest wymagana')
    .max(50, 'Nazwa subskrypcji może mieć maksymalnie 50 znaków'),
  price: Yup.number().required('Cena subskrypcji jest wymagana').min(0, 'Cena musi być dodatnia'),
});

export const DiscountsTab = () => {
  const [selectedSubscriptionId, setSelectedSubscriptionId] = useState<number | null>(null);
  const { showNotification, NotificationComponent } = useNotification();

  const {
    control,
    handleSubmit,
    formState: { errors },
    reset,
  } = useForm<NewSubscriptionFormData>({
    resolver: yupResolver(validationSchema),
    defaultValues: {
      name: '',
      price: undefined,
    },
  });

  const { mutateAsync: createSubscription, isPending: isCreateSubscriptionLoading } =
    useCreateSubscription();

  const {
    data: allSubscriptions,
    isLoading: isAllSubscriptionsLoading,
    isError: isAllSubscriptionsError,
    refetch: refetchAllSubscriptions,
  } = useGetAllSubscriptions();

  const {
    data: allServiceSubscriptionDiscounts,
    isLoading: isAllServiceSubscriptionDiscountsLoading,
    isError: isAllServiceSubscriptionDiscountsError,
    refetch: refefetchAllServiceSubscriptionDiscounts,
  } = useGetAllServiceSubscriptionDiscounts();

  const {
    data: allServices,
    isLoading: isAllServicesLoading,
    isError: isAllServicesError,
  } = useGetAllServices();

  const handleUpdateDiscount = async (
    subscriptionId: number,
    serviceId: number,
    discount: number,
  ) => {
    try {
      if (!selectedSubscriptionId) {
        showNotification('Wybierz najpierw abonament', 'error');
        return;
      }

      await updateServiceDiscount(subscriptionId, serviceId, discount);
      await refefetchAllServiceSubscriptionDiscounts();
      showNotification('Zaktualizowano zniżkę', 'success');
    } catch (error) {
      showNotification('Błąd podczas aktualizacji zniżki', 'error');
      console.error('Error updating discount:', error);
    }
  };

  const handleCreateSubscription = async (data: NewSubscriptionFormData) => {
    try {
      await createSubscription({ params: { name: data.name, price: data.price } });
      await refetchAllSubscriptions();
      showNotification('Udało się dodać abonament!', 'success');
      reset();
    } catch (error) {
      showNotification('Błąd podczas dodawania abonamentu', 'error');
      console.error(error);
    }
  };

  useEffect(() => {
    if (isAllSubscriptionsError || isAllServiceSubscriptionDiscountsError || isAllServicesError) {
      showNotification('Coś poszło nie tak', 'error');
    }
  }, [isAllSubscriptionsError, isAllServiceSubscriptionDiscountsError, isAllServicesError]);

  const selected = allSubscriptions?.find((s) => s.id === selectedSubscriptionId);

  if (
    isAllSubscriptionsLoading ||
    !allSubscriptions ||
    isAllServiceSubscriptionDiscountsLoading ||
    !allServiceSubscriptionDiscounts ||
    isAllServicesLoading ||
    !allServices ||
    isCreateSubscriptionLoading
  ) {
    return (
      <>
        <FullPageLoadingSpinner />
        <NotificationComponent />
      </>
    );
  }

  return (
    <Box sx={{ display: 'flex', gap: 3 }}>
      <Box
        sx={{
          display: 'flex',
          flexDirection: 'column',
          justifyContent: 'space-between',
          width: 400,
        }}
      >
        <Select
          value={selectedSubscriptionId || ''}
          onChange={(e) => setSelectedSubscriptionId(Number(e.target.value))}
          displayEmpty
        >
          {allSubscriptions.map((sub) => (
            <MenuItem key={sub.id} value={sub.id}>
              {sub.name}
            </MenuItem>
          ))}
        </Select>
        {selected && <Typography variant="body2">Cena: {selected.price.toFixed(2)} zł</Typography>}
        <Box
          component="form"
          onSubmit={handleSubmit(handleCreateSubscription)}
          sx={{ mt: 4, display: 'flex', flexDirection: 'column', gap: 2 }}
        >
          <Typography variant="h6">Dodaj subskrypcję</Typography>
          <Controller
            name="name"
            control={control}
            render={({ field }) => (
              <TextField
                {...field}
                label="Nazwa subskrypcji"
                fullWidth
                error={!!errors.name}
                helperText={errors.name?.message}
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
          <Button
            type="submit"
            variant="contained"
            disabled={isCreateSubscriptionLoading}
            loading={isCreateSubscriptionLoading}
            sx={{ mt: 2 }}
          >
            Dodaj subskrypcję
          </Button>
        </Box>
      </Box>
      <Box sx={{ flex: 1, height: 500 }}>
        <SubscriptionServicesTable
          allServices={allServices}
          subscriptionId={selectedSubscriptionId}
          onUpdateDiscount={handleUpdateDiscount}
        />
      </Box>
      <NotificationComponent />
    </Box>
  );
};
