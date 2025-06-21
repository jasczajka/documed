import { Box, Button, Stack, TextField } from '@mui/material';
import { DataGrid, GridColDef } from '@mui/x-data-grid';
import { FC, useMemo, useState } from 'react';
import { Service } from 'shared/api/generated/generated.schemas';
import { useGetAllServiceSubscriptionDiscounts } from 'shared/api/generated/subscription-controller/subscription-controller';
import { useModal } from 'shared/hooks/useModal';
import { DiscountEditModal } from './subcomponents/DiscountEditModal';

export interface ServiceWithDiscount {
  id: number;
  name: string;
  discount: number;
  originalPrice: number;
  discountedPrice: number;
}

interface SubscriptionServicesTableProps {
  allServices: Service[];
  subscriptionId?: number | null;
  onUpdateDiscount?: (subscriptionId: number, serviceId: number, discount: number) => Promise<void>;
}

export const SubscriptionServicesTable: FC<SubscriptionServicesTableProps> = ({
  allServices,
  subscriptionId,
  onUpdateDiscount,
}) => {
  const { data: allServiceSubscriptionDiscounts } = useGetAllServiceSubscriptionDiscounts();
  const { openModal } = useModal();
  const [search, setSearch] = useState('');

  const servicesWithDiscounts = useMemo(() => {
    if (!allServices || !allServiceSubscriptionDiscounts || !subscriptionId) {
      return [];
    }

    return allServices.map((service) => {
      const discount =
        allServiceSubscriptionDiscounts.find(
          (d) => d.subscriptionId === subscriptionId && d.serviceId === service.id,
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
  }, [allServices, allServiceSubscriptionDiscounts, subscriptionId]);

  const filteredRows = useMemo(() => {
    const lower = search.toLowerCase();
    return servicesWithDiscounts.filter((s) => s.name.toLowerCase().includes(lower));
  }, [servicesWithDiscounts, search]);

  const handleOpenEditModal = (service: ServiceWithDiscount) => {
    if (!subscriptionId) {
      return;
    }
    openModal(`editDiscountModal`, (close) => (
      <DiscountEditModal
        service={service}
        subscriptionId={subscriptionId}
        onSave={async (subscriptionId, serviceId, discount) => {
          if (onUpdateDiscount) {
            await onUpdateDiscount(subscriptionId, serviceId, discount);
          }
          close();
        }}
        onCancel={close}
      />
    ));
  };

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
    ...(onUpdateDiscount
      ? [
          {
            field: 'actions',
            headerName: 'Akcje',
            width: 150,
            renderCell: (params: { row: ServiceWithDiscount }) => (
              <Button
                variant="outlined"
                size="small"
                onClick={() => handleOpenEditModal(params.row)}
              >
                Edytuj zniżkę
              </Button>
            ),
          },
        ]
      : []),
  ];

  return (
    <Stack spacing={2}>
      <TextField
        label="Szukaj usługi"
        variant="outlined"
        fullWidth
        value={search}
        onChange={(e) => setSearch(e.target.value)}
      />
      <Box sx={{ flex: 1, height: 500 }}>
        <DataGrid
          rows={filteredRows}
          columns={columns}
          pageSizeOptions={[5]}
          initialState={{
            pagination: {
              paginationModel: { pageSize: 5, page: 0 },
            },
            sorting: {
              sortModel: [{ field: 'name', sort: 'asc' }],
            },
          }}
          disableRowSelectionOnClick
        />
      </Box>
    </Stack>
  );
};
