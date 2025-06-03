import { Delete } from '@mui/icons-material';
import { Box, Button, IconButton, Paper, Typography } from '@mui/material';
import { DataGrid, GridColDef } from '@mui/x-data-grid';
import { FC, useEffect, useMemo, useState } from 'react';
import {
  CreateReferralDTO,
  ReferralType,
  ReferralTypeDTO,
  ReturnReferralDTO,
} from 'shared/api/generated/generated.schemas';
import { useModal } from 'shared/hooks/useModal';
import { AddReferralToVisitModal } from './AddReferralToVisitModal';

export type ReferralWithTempId = (ReturnReferralDTO | CreateReferralDTO) & {
  tempId?: string;
  id?: number;
};
interface VisitReferralsTableProps {
  referralTypes: ReferralTypeDTO[];
  visitId: number;
  existingReferrals?: ReturnReferralDTO[];
  onRemoveReferral: (referralId: number) => void;
  onPendingRemoveReferral: (tempId: string) => void;
  onAddReferral: (newReferral: ReferralWithTempId) => void;
  disabled?: boolean;
}

export const VisitReferralsTable: FC<VisitReferralsTableProps> = ({
  referralTypes,
  visitId,
  existingReferrals = [],
  onRemoveReferral,
  onPendingRemoveReferral,
  onAddReferral,
  disabled = false,
}) => {
  const [referrals, setReferrals] = useState<ReferralWithTempId[]>(existingReferrals);

  console.log('referrals: ', referrals);
  const { openModal } = useModal();

  const referralTypeMap = useMemo(() => {
    const map = new Map<ReferralType, string>();
    referralTypes.forEach((type) => {
      if (type.code) {
        map.set(type.code, type.description || type.code.toString());
      }
    });
    return map;
  }, [referralTypes]);

  const handleAddReferralClick = () => {
    openModal('addReferralModal', (close) => (
      <AddReferralToVisitModal
        referralTypes={referralTypes}
        onSubmit={(data) => {
          const tempId = `temp-${Date.now()}`;
          const newReferral: ReferralWithTempId = {
            ...data,
            visitId,
            tempId,
            expirationDate: data.expirationDate.toISOString(),
          };

          setReferrals((prev) => [...prev, newReferral]);

          const createReferralDto: CreateReferralDTO = {
            visitId,
            type: data.type,
            diagnosis: data.diagnosis,
            expirationDate: data.expirationDate.toISOString(),
          };

          onAddReferral(createReferralDto);
          close();
        }}
        onCancel={close}
      />
    ));
  };

  const handleRemoveReferral = (referral: ReferralWithTempId) => {
    setReferrals((prev) =>
      prev.filter(
        (r) => (r.id && r.id !== referral.id) || (r.tempId && r.tempId !== referral.tempId),
      ),
    );

    if (referral.id) {
      onRemoveReferral(referral.id);
      return;
    }
    if (referral.tempId) {
      onPendingRemoveReferral(referral.tempId);
    }
  };

  useEffect(() => {
    setReferrals(existingReferrals);
  }, [existingReferrals]);

  const columns: GridColDef<ReferralWithTempId>[] = useMemo(
    () => [
      {
        field: 'type',
        headerName: 'Rodzaj',
        flex: 1,
        valueGetter: (_, row) => {
          return referralTypeMap.get(row.type);
        },
      },
      {
        field: 'expirationDate',
        headerName: 'Data ważności',
        flex: 1,
        valueGetter: (_, row) => {
          const date = row.expirationDate;
          return new Date(date).toLocaleDateString();
        },
      },
      {
        field: 'diagnosis',
        headerName: 'Diagnoza',
        flex: 1,
        valueGetter: (_, row) => row.diagnosis,
      },
      ...(disabled
        ? []
        : [
            {
              field: 'actions',
              headerName: 'Akcje',
              flex: 0.5,
              renderCell: ({ row }: { row: ReferralWithTempId }) => (
                <IconButton onClick={() => handleRemoveReferral(row)} disabled={disabled}>
                  <Delete />
                </IconButton>
              ),
            },
          ]),
    ],
    [disabled, referralTypeMap],
  );

  return (
    <Paper sx={{ height: '100%' }}>
      <Box
        sx={{
          width: '100%',
          p: 2,
          display: 'flex',
          flexDirection: 'column',
          gap: 2,
          alignItems: 'flex-start',
        }}
      >
        <Typography variant="h6" sx={{ mb: 1 }}>
          Skierowania
        </Typography>
        <Box sx={{ width: '100%', alignItems: 'flex-start', display: 'flex', gap: 8 }}>
          {!disabled && (
            <Button variant="contained" onClick={handleAddReferralClick}>
              Dodaj skierowanie
            </Button>
          )}
        </Box>

        <DataGrid
          rows={referrals}
          columns={columns}
          getRowId={(row) => row.id || row.tempId || ''}
          pageSizeOptions={[5, 10]}
          disableRowSelectionOnClick
          disableColumnFilter
          sx={{
            width: '100%',
          }}
        />
      </Box>
    </Paper>
  );
};
