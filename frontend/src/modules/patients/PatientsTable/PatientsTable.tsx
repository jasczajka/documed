import { Box, Button, Link, Paper } from '@mui/material';
import { DataGrid, GridColDef } from '@mui/x-data-grid';

import { FC, useCallback, useState } from 'react';
import { useNavigate } from 'react-router';
import { PatientDetailsDTO, Subscription } from 'shared/api/generated/generated.schemas';
import { ScheduleVisitModal } from 'shared/components/ScheduleVisitModal/ScheduleVisitModal';
import { TableFilters } from 'shared/components/TableFilters';
import { useSubscriptionStore } from 'shared/hooks/stores/useSubscriptionStore';
import { useAuth } from 'shared/hooks/useAuth';
import { useModal } from 'shared/hooks/useModal';
import { useNotification } from 'shared/hooks/useNotification';
import { useSitemap } from 'shared/hooks/useSitemap';
import { getAgeFromBirthDate } from 'shared/utils/getAgeFromBirthDate';
import { usePatientsTable } from './usePatientsTable';

export type PatientsFilters = {
  patientName: string;
  pesel: string;
};

interface PatientsTableProps {
  patients: PatientDetailsDTO[];
}

const columns = (
  allSubscriptions: Subscription[],
  onNavigateToPatient: (patientId: number) => void,
  onScheduleVisitClick: (patientDetails: PatientDetailsDTO) => void,
  canScheduleVisit: boolean,
): GridColDef<PatientDetailsDTO>[] => [
  {
    field: 'index',
    headerName: '#',
    width: 70,
    valueGetter: (_value, row, _, apiRef) =>
      apiRef.current.getRowIndexRelativeToVisibleRows(row.id) + 1,
  },
  {
    field: 'patientName',
    headerName: 'Imię i nazwisko',
    minWidth: 200,
    flex: 1,
    valueGetter: (_value, row) => `${row.firstName} ${row.lastName}`,
    renderCell: ({ row }) => (
      <Link
        component="button"
        onClick={() => onNavigateToPatient(row.id)}
        underline="hover"
        color="primary"
        sx={{ cursor: 'pointer', fontWeight: 500 }}
      >
        {`${row.firstName} ${row.lastName}`}
      </Link>
    ),
  },
  {
    field: 'pesel',
    headerName: 'PESEL',
    minWidth: 200,
    flex: 1,
    valueGetter: (_, row) => row.pesel ?? 'Brak',
  },
  {
    field: 'subscription',
    headerName: 'Abonament',
    minWidth: 200,
    flex: 1,
    valueGetter: (_, row) => {
      const subscription = allSubscriptions.find((s) => s.id === row.subscriptionId);
      return subscription?.name ?? 'Brak';
    },
  },
  {
    field: 'actions',
    headerName: 'Akcje',
    flex: 0.5,
    renderCell: ({ row }) => {
      return canScheduleVisit ? (
        <Button onClick={() => onScheduleVisitClick(row)}>Umów wizytę</Button>
      ) : (
        <span style={{ color: '#999' }}>Brak akcji</span>
      );
    },
  },
];

export const PatientsTable: FC<PatientsTableProps> = ({ patients }) => {
  const [filters, setFilters] = useState<PatientsFilters>({
    patientName: '',
    pesel: '',
  });

  const navigate = useNavigate();
  const { isWardClerk, isPatient } = useAuth();
  const sitemap = useSitemap();
  const { showNotification, NotificationComponent } = useNotification();
  const { openModal } = useModal();
  const allSubscriptions = useSubscriptionStore((state) => state.subscriptions);
  const { patientsFilterConfig, filteredPatients } = usePatientsTable({
    patients,
    filters,
  });

  const handleFilterChange = useCallback((name: keyof PatientsFilters, value: string) => {
    setFilters((prev) => ({ ...prev, [name]: value }));
  }, []);

  const handleScheduleVisitClick = useCallback(
    async (patientDetails: PatientDetailsDTO) => {
      openModal('scheduleVisitModal', (close) => (
        <ScheduleVisitModal
          patientId={patientDetails.id}
          patientFullName={`${patientDetails.firstName} ${patientDetails.lastName}`}
          patientAge={
            patientDetails?.birthdate
              ? getAgeFromBirthDate(new Date(patientDetails.birthdate))
              : null
          }
          onConfirm={async () => {
            close();
            showNotification('Umówiono wizytę', 'success');
          }}
          onCancel={close}
        />
      ));
    },
    [openModal],
  );

  const onNavigateToPatient = useCallback(
    (id: number) => {
      navigate(sitemap.patient(id));
    },
    [navigate],
  );

  const resetFilters = useCallback(() => {
    setFilters({
      patientName: '',
      pesel: '',
    });
  }, []);

  return (
    <Box sx={{ height: '100%', width: '100%', display: 'flex', flexDirection: 'column', p: 2 }}>
      <TableFilters<PatientsFilters>
        filters={filters}
        filterConfig={patientsFilterConfig}
        onFilterChange={handleFilterChange}
        onReset={resetFilters}
      />
      <Paper sx={{ flexGrow: 1 }}>
        <DataGrid
          rows={filteredPatients}
          columns={columns(
            allSubscriptions,
            onNavigateToPatient,
            (patientDetails: PatientDetailsDTO) => handleScheduleVisitClick(patientDetails),
            isWardClerk || isPatient,
          )}
          initialState={{
            pagination: {
              paginationModel: { page: 0, pageSize: 10 },
            },
            sorting: {
              sortModel: [{ field: 'specialistName', sort: 'asc' }],
            },
          }}
          pageSizeOptions={[5, 10, 25]}
          paginationMode="client"
          rowHeight={32}
          disableColumnFilter
          disableRowSelectionOnClick
        />
      </Paper>
      <NotificationComponent />
    </Box>
  );
};

export default PatientsTable;
