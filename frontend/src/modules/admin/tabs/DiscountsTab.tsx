import { Box, MenuItem, Select, Typography } from '@mui/material';
import { useEffect, useState } from 'react';
import { useGetAllServices } from 'shared/api/generated/service-controller/service-controller';
import {
  updateServiceDiscount,
  useGetAllServiceSubscriptionDiscounts,
  useGetAllSubscriptions,
} from 'shared/api/generated/subscription-controller/subscription-controller';
import { FullPageLoadingSpinner } from 'shared/components/FullPageLoadingSpinner';
import { SubscriptionServicesTable } from 'shared/components/SubscriptionServicesTable/SubscriptionServicesTable';
import { useNotification } from 'shared/hooks/useNotification';

export const DiscountsTab = () => {
  const [selectedSubscriptionId, setSelectedSubscriptionId] = useState<number | null>(null);

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
    !allServices
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
      <Box sx={{ display: 'flex', flexDirection: 'column', gap: 3, width: 400 }}>
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
