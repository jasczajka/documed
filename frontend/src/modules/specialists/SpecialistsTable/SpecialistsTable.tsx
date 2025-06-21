import { Box, Button, Chip, Link, Paper, Tooltip, Typography } from '@mui/material';
import { DataGrid, GridColDef } from '@mui/x-data-grid';
import { useCallback, useState } from 'react';
import { useNavigate } from 'react-router';
import { DoctorDetailsDTO } from 'shared/api/generated/generated.schemas';
import { getPatientDetails } from 'shared/api/generated/patients-controller/patients-controller';
import { ScheduleVisitModal } from 'shared/components/ScheduleVisitModal/ScheduleVisitModal';
import { TableFilters } from 'shared/components/TableFilters';
import { useAuthStore } from 'shared/hooks/stores/useAuthStore';
import { useDoctorsStore } from 'shared/hooks/stores/useDoctorsStore';
import { useSpecializationsStore } from 'shared/hooks/stores/useSpecializationsStore';
import { useAuth } from 'shared/hooks/useAuth';
import { useModal } from 'shared/hooks/useModal';
import { useNotification } from 'shared/hooks/useNotification';
import { useSitemap } from 'shared/hooks/useSitemap';
import { getAgeFromBirthDate } from 'shared/utils/getAgeFromBirthDate';
import { useSpecialistsTable } from './useSpecialistsTable';

const MAX_SPECIALIZATIONS_PER_ROW = 9;

export type SpecialistFilters = {
  specialistName: string;
  specialization: string;
};

const columns = (
  canNavigateToSpecialist: boolean,
  onNavigateToSpecialist: (specialistId: number) => void,
  onScheduleVisitClick?: (specialistId: number) => void,
): GridColDef<DoctorDetailsDTO>[] => [
  {
    field: 'index',
    headerName: '#',
    width: 70,
    valueGetter: (_value, row, _, apiRef) =>
      apiRef.current.getRowIndexRelativeToVisibleRows(row.id) + 1,
  },
  {
    field: 'specialistName',
    headerName: 'Imię i nazwisko',
    minWidth: 200,
    flex: 0.25,
    valueGetter: (_value, row) => `${row.firstName} ${row.lastName}`,
    renderCell: ({ row }) =>
      canNavigateToSpecialist ? (
        <Link
          component="button"
          onClick={() => onNavigateToSpecialist(row.id)}
          underline="hover"
          color="primary"
          sx={{ cursor: 'pointer', fontWeight: 500 }}
        >
          {`${row.firstName} ${row.lastName}`}
        </Link>
      ) : (
        <Typography>{`${row.firstName} ${row.lastName}`}</Typography>
      ),
  },
  {
    field: 'specializations',
    headerName: 'Specjalizacje',
    minWidth: 200,
    flex: 1,
    renderCell: ({ row }) => (
      <Box
        sx={{
          display: 'flex',
          alignItems: 'center',
          width: '100%',
          height: '100%',
          minHeight: 32,
        }}
      >
        <Box
          sx={{
            display: 'flex',
            gap: '4px',
            alignItems: 'center',
            maxWidth: '100%',
            overflow: 'hidden',
          }}
        >
          {row.specializations.slice(0, MAX_SPECIALIZATIONS_PER_ROW).map((spec) => (
            <Chip key={spec.id} label={spec.name} size="small" />
          ))}
          {row.specializations.length > MAX_SPECIALIZATIONS_PER_ROW && (
            <Tooltip
              title={
                <Box sx={{ p: 2 }}>
                  <Typography sx={{ pb: 2 }} variant="subtitle2" gutterBottom>
                    Wszystkie specjalizacje:
                  </Typography>
                  <Box sx={{ display: 'flex', flexWrap: 'wrap', gap: 1 }}>
                    {row.specializations.map((spec) => (
                      <Chip
                        key={spec.id}
                        label={spec.name}
                        size="small"
                        sx={{
                          color: 'white',
                          backgroundColor: 'primary.main',
                          border: 'none',
                        }}
                        variant="outlined"
                      />
                    ))}
                  </Box>
                </Box>
              }
              placement="top-start"
              enterDelay={300}
              disableInteractive
            >
              <Chip
                label={`...`}
                size="small"
                sx={{
                  backgroundColor: 'action.selected',
                  color: 'text.primary',
                  cursor: 'pointer',
                  '&:hover': {
                    backgroundColor: 'action.hover',
                  },
                }}
              />
            </Tooltip>
          )}
        </Box>
      </Box>
    ),
  },
  ...(onScheduleVisitClick
    ? [
        {
          field: 'actions',
          headerName: 'Akcje',
          flex: 0.5,
          renderCell: ({ row }: { row: DoctorDetailsDTO }) => (
            <Button onClick={() => onScheduleVisitClick(row.id)}>Umów wizytę</Button>
          ),
        },
      ]
    : []),
];

export const SpecialistsTable = () => {
  const [filters, setFilters] = useState<SpecialistFilters>({
    specialistName: '',
    specialization: '',
  });

  const navigate = useNavigate();
  const sitemap = useSitemap();
  const { showNotification, NotificationComponent } = useNotification();
  const { openModal } = useModal();
  const { isWardClerk, isDoctor } = useAuth();
  const userId = useAuthStore((state) => state.user!.id);

  const specialists = useDoctorsStore((state) => state.doctors);
  const specializations = useSpecializationsStore((state) => state.specializations);

  const { specialistsFilterConfig, filteredSpecialists } = useSpecialistsTable({
    specialists,
    specializations,
    filters,
  });

  const handleFilterChange = useCallback((name: keyof SpecialistFilters, value: string) => {
    setFilters((prev) => ({ ...prev, [name]: value }));
  }, []);

  const handleScheduleVisitClick = useCallback(
    async (specialistId: number) => {
      const patientDetails = await getPatientDetails(userId);
      openModal('scheduleVisitModal', (close) => (
        <ScheduleVisitModal
          patientId={patientDetails.id}
          patientFullName={`${patientDetails.firstName} ${patientDetails.lastName}`}
          patientAge={
            patientDetails?.birthdate
              ? getAgeFromBirthDate(new Date(patientDetails.birthdate))
              : null
          }
          initialDoctorId={specialistId}
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

  const onNavigateToSpecialist = useCallback(
    (id: number) => {
      navigate(sitemap.specialist(id));
    },
    [navigate],
  );

  const resetFilters = useCallback(() => {
    setFilters({
      specialistName: '',
      specialization: '',
    });
  }, []);

  return (
    <Box sx={{ height: '100%', width: '100%', display: 'flex', flexDirection: 'column', p: 2 }}>
      <TableFilters<SpecialistFilters>
        filters={filters}
        filterConfig={specialistsFilterConfig}
        onFilterChange={handleFilterChange}
        onReset={resetFilters}
      />
      <Paper sx={{ flexGrow: 1 }}>
        <DataGrid
          rows={filteredSpecialists}
          columns={columns(
            isWardClerk || isDoctor,
            onNavigateToSpecialist,
            isWardClerk
              ? undefined
              : (specialistId: number) => handleScheduleVisitClick(specialistId),
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

export default SpecialistsTable;
