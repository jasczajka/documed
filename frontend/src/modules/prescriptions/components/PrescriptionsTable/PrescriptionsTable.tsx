import { Box, Button, FormControlLabel, Paper, Switch } from '@mui/material';
import { DataGrid, GridColDef } from '@mui/x-data-grid';

import { format, subDays } from 'date-fns';
import { FC, useCallback, useMemo, useState } from 'react';
import { Prescription } from 'shared/api/generated/generated.schemas';
import { getMedicinesForPrescription } from 'shared/api/generated/prescription-controller/prescription-controller';
import { PrescriptionMedicinesModal } from 'shared/components/PrescriptionMedicinesModal';
import { useModal } from 'shared/hooks/useModal';

export type PrescriptionFilters = {
  showOnlyNotExpired: boolean;
};

interface PrescriptionsTableProps {
  prescriptions: Prescription[];
}

const columns = (
  onShowPrescription: (id: number, accessCode: number) => void,
  loading?: boolean,
): GridColDef<Prescription>[] => [
  {
    field: 'doctorName',
    headerName: 'Wystawione przez',
    minWidth: 200,
    flex: 1,
    valueGetter: (_, row) => row.issuingDoctorFullName,
  },
  {
    field: 'issueDate',
    headerName: 'Wystawione',
    minWidth: 200,
    flex: 1,
    valueGetter: (_, row) => format(new Date(row.date), 'dd.MM.yyyy'),
  },
  {
    field: 'expirationDate',
    headerName: 'Ważne do',
    minWidth: 200,
    flex: 1,
    valueGetter: (_, row) => format(new Date(row.expirationDate), 'dd.MM.yyyy'),
  },

  {
    field: 'actions',
    headerName: 'Akcje',
    minWidth: 160,
    flex: 0.7,
    renderCell: ({ row }) => (
      <Button
        variant="text"
        size="small"
        onClick={() => onShowPrescription(row.id, row.accessCode)}
        loading={loading}
        disabled={loading}
      >
        Wyświetl receptę
      </Button>
    ),
  },
];

export const PrescriptionsTable: FC<PrescriptionsTableProps> = ({ prescriptions }) => {
  const [filters, setFilters] = useState<PrescriptionFilters>({
    showOnlyNotExpired: true,
  });
  const [isLoading, setIsLoading] = useState(false);

  const filteredPrescriptions = useMemo(() => {
    if (!filters.showOnlyNotExpired) return prescriptions;

    const yesterday = subDays(new Date(), 1);
    return prescriptions.filter((p) => new Date(p.expirationDate) > yesterday);
  }, [prescriptions, filters]);

  const handleToggleFilter = () => {
    setFilters((prev) => ({
      ...prev,
      showOnlyNotExpired: !prev.showOnlyNotExpired,
    }));
  };

  const { openModal } = useModal();

  const handleShowPrescriptionClick = useCallback(
    async (id: number, accessCode: number) => {
      setIsLoading(true);

      const medicines = await getMedicinesForPrescription(id);

      setIsLoading(false);

      openModal('prescriptionMedicinesModal', (close) => (
        <PrescriptionMedicinesModal
          accessCode={accessCode.toString()}
          medicines={medicines ?? []}
          onCancel={close}
        />
      ));
    },
    [openModal],
  );

  return (
    <Box sx={{ height: '100%', width: '100%', display: 'flex', flexDirection: 'column', p: 2 }}>
      <Box sx={{ pb: 2 }}>
        <FormControlLabel
          control={<Switch checked={filters.showOnlyNotExpired} onChange={handleToggleFilter} />}
          label="Pokaż tylko ważne recepty"
        />
      </Box>

      <Paper sx={{ flexGrow: 1 }}>
        <DataGrid
          rows={filteredPrescriptions}
          columns={columns(handleShowPrescriptionClick, isLoading)}
          initialState={{
            pagination: {
              paginationModel: { page: 0, pageSize: 10 },
            },
            sorting: {
              sortModel: [{ field: 'issueDate', sort: 'asc' }],
            },
          }}
          pageSizeOptions={[5, 10, 25]}
          paginationMode="client"
          rowHeight={32}
          disableColumnFilter
          disableRowSelectionOnClick
        />
      </Paper>
    </Box>
  );
};
