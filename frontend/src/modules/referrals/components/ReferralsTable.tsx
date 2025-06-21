import { Box, Button, FormControlLabel, Paper, Switch } from '@mui/material';
import { DataGrid, GridColDef } from '@mui/x-data-grid';

import { format, subDays } from 'date-fns';
import { FC, useCallback, useMemo, useState } from 'react';
import { ReturnReferralDTO } from 'shared/api/generated/generated.schemas';
import { useModal } from 'shared/hooks/useModal';
import { ReferralModal } from './ReferralModal/ReferralModal';

export type ReferralFilters = {
  showOnlyNotExpired: boolean;
};

interface ReferralsTableProps {
  referrals: ReturnReferralDTO[];
}

const columns = (
  onShowReferral: (referral: ReturnReferralDTO) => void,
): GridColDef<ReturnReferralDTO>[] => [
  {
    field: 'doctorName',
    headerName: 'Wystawione przez',
    minWidth: 200,
    flex: 1,
    valueGetter: (_, row) => row.issuingDoctorFullName,
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
      <Button variant="text" size="small" onClick={() => onShowReferral(row)}>
        Wyświetl szczegóły
      </Button>
    ),
  },
];

export const ReferralsTable: FC<ReferralsTableProps> = ({ referrals }) => {
  const [filters, setFilters] = useState<ReferralFilters>({
    showOnlyNotExpired: true,
  });

  const filteredPrescriptions = useMemo(() => {
    if (!filters.showOnlyNotExpired) {
      return referrals;
    }

    const yesterday = subDays(new Date(), 1);
    return referrals.filter((r) => new Date(r.expirationDate) > yesterday);
  }, [referrals, filters]);

  const handleToggleFilter = () => {
    setFilters((prev) => ({
      ...prev,
      showOnlyNotExpired: !prev.showOnlyNotExpired,
    }));
  };

  const { openModal } = useModal();

  const handleShowReferralClick = useCallback(
    async (referral: ReturnReferralDTO) => {
      openModal('referralModal', (close) => (
        <ReferralModal
          initialData={{
            expirationDate: new Date(referral.expirationDate),
            diagnosis: referral.diagnosis,
            type: referral.type,
          }}
          onSubmit={() => {}}
          onCancel={close}
          readOnly
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
          label="Pokaż tylko ważne skierowania"
        />
      </Box>

      <Paper sx={{ flexGrow: 1 }}>
        <DataGrid
          rows={filteredPrescriptions}
          columns={columns(handleShowReferralClick)}
          initialState={{
            pagination: {
              paginationModel: { page: 0, pageSize: 10 },
            },
            sorting: {
              sortModel: [{ field: 'expirationDate', sort: 'asc' }],
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
