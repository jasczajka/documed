import { yupResolver } from '@hookform/resolvers/yup';
import { Box, Button, FormControl, MenuItem, Select, Typography } from '@mui/material';
import { DataGrid, GridColDef } from '@mui/x-data-grid';
import { FC, useEffect, useMemo } from 'react';
import { Controller, useForm } from 'react-hook-form';
import { useParams } from 'react-router';
import { Service } from 'shared/api/generated/generated.schemas';
import {
  removeUserSubscription,
  updateUserSubscription,
} from 'shared/api/generated/patients-controller/patients-controller';
import {
  useGetAllServiceSubscriptionDiscounts,
  useGetAllSubscriptions,
} from 'shared/api/generated/subscription-controller/subscription-controller';
import { FullPageLoadingSpinner } from 'shared/components/FullPageLoadingSpinner';
import { useNotification } from 'shared/hooks/useNotification';
import * as Yup from 'yup';

type FormData = {
  subscriptionId?: number | null;
};
const validationSchema = Yup.object({
  subscriptionId: Yup.number().nullable(),
});

interface SubscriptionTabProps {
  patientSubscriptionId: number | null;
  allServices: Service[];
  refetch: () => void;
}

interface ServiceWithDiscount {
  id: number;
  name: string;
  discount: number;
  originalPrice: number;
  discountedPrice: number;
}

export const SubscriptionTab: FC<SubscriptionTabProps> = ({
  patientSubscriptionId,
  allServices,
  refetch,
}) => {
  const {
    control,
    handleSubmit,
    formState: { errors },
    watch,
  } = useForm({
    resolver: yupResolver(validationSchema),
    defaultValues: {
      subscriptionId: patientSubscriptionId || null,
    },
  });

  const { id } = useParams<{ id: string }>();
  const patientId = Number(id);

  const { showNotification, NotificationComponent } = useNotification();
  const {
    data: allSubscriptions,
    isLoading: isAllSubscriptionsLoading,
    isError: isAllSubscriptionsError,
  } = useGetAllSubscriptions();

  const {
    data: allServiceSubscriptionDiscounts,
    isLoading: isAllServiceSubscriptionDiscountsLoading,
    isError: isAllServiceSubscriptionDiscountsError,
  } = useGetAllServiceSubscriptionDiscounts();

  useEffect(() => {
    if (isAllSubscriptionsError || isAllServiceSubscriptionDiscountsError) {
      showNotification('Coś poszło nie tak', 'error');
    }
  }, [isAllSubscriptionsError, isAllServiceSubscriptionDiscountsError]);

  const selectedId = watch('subscriptionId');
  const selected = allSubscriptions?.find((s) => s.id === selectedId);

  const servicesWithDiscounts = useMemo(() => {
    if (!allServices || !allServiceSubscriptionDiscounts || !selectedId) return [];

    return allServices.map((service) => {
      const discount =
        allServiceSubscriptionDiscounts.find(
          (d) => d.subscriptionId === selectedId && d.serviceId === service.id,
        )?.discount || 0;

      const originalPrice = service.price || 0;
      const discountedPrice = originalPrice * (1 - discount / 100);

      return {
        id: service.id,
        name: service.name || '',
        discount,
        originalPrice,
        discountedPrice,
      };
    });
  }, [allServices, allServiceSubscriptionDiscounts, selectedId]);

  const columns: GridColDef<ServiceWithDiscount>[] = [
    {
      field: 'name',
      headerName: 'Nazwa usługi',
      flex: 1,
    },
    {
      field: 'discount',
      headerName: 'Zniżka (%)',
      width: 150,
      valueGetter: (_, row) => `${row.discount}%`,
    },
    {
      field: 'originalPrice',
      headerName: 'Cena oryginalna',
      width: 150,
      valueGetter: (_, row) => `${row.originalPrice.toFixed(2)} zł`,
    },
    {
      field: 'discountedPrice',
      headerName: 'Cena po zniżce',
      width: 180,
      valueGetter: (_, row) => `${row.discountedPrice.toFixed(2)} zł`,
    },
  ];

  const onSubmit = async (data: FormData) => {
    try {
      if (!data.subscriptionId) {
        await removeUserSubscription(patientId);
        showNotification('Usunięto abonament', 'success');
      } else {
        await updateUserSubscription(patientId, data.subscriptionId);
        showNotification('Zaktualizowano abonament', 'success');
      }
      refetch();
    } catch (error) {
      console.error('Error updating subscription:', error);
      showNotification('Coś poszło nie tak', 'error');
    }
  };

  if (
    isAllSubscriptionsLoading ||
    !allSubscriptions ||
    isAllServiceSubscriptionDiscountsLoading ||
    !allServiceSubscriptionDiscounts
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
        component="form"
        onSubmit={handleSubmit(onSubmit)}
        sx={{ display: 'flex', flexDirection: 'column', gap: 3, width: 400 }}
      >
        <FormControl fullWidth error={!!errors.subscriptionId}>
          <Controller
            name="subscriptionId"
            control={control}
            render={({ field }) => (
              <Select {...field} displayEmpty value={field.value ?? null}>
                <MenuItem value={null as unknown as string}>Brak abonamentu</MenuItem>
                {allSubscriptions.map((sub) => (
                  <MenuItem key={sub.id} value={sub.id}>
                    {sub.name}
                  </MenuItem>
                ))}
              </Select>
            )}
          />
          {errors.subscriptionId && (
            <Typography color="error" variant="caption">
              {errors.subscriptionId.message}
            </Typography>
          )}
        </FormControl>

        {selected && <Typography variant="body2">Cena: {selected.price.toFixed(2)} zł</Typography>}

        <Button variant="contained" type="submit">
          Zapisz
        </Button>
      </Box>
      <Box sx={{ flex: 1, height: 500 }}>
        <DataGrid
          rows={servicesWithDiscounts}
          columns={columns}
          pageSizeOptions={[10, 25, 50]}
          initialState={{
            pagination: {
              paginationModel: { page: 0, pageSize: 10 },
            },
          }}
          disableRowSelectionOnClick
        />
      </Box>

      <NotificationComponent />
    </Box>
  );
};
