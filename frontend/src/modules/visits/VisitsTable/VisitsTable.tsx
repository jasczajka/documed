import { Box, Button, FormControlLabel, Link, Paper, Switch } from '@mui/material';
import { DataGrid, GridActionsCellItem, GridColDef } from '@mui/x-data-grid';

import { endOfDay, format, parse, startOfDay } from 'date-fns';
import { getVisitStatusLabel } from 'modules/visit/utils';
import { FC, useCallback, useState } from 'react';
import { useNavigate } from 'react-router';
import { VisitWithDetails, VisitWithDetailsStatus } from 'shared/api/generated/generated.schemas';
import { appConfig } from 'shared/appConfig';
import { TableFilters } from 'shared/components/TableFilters';
import { useAuthStore } from 'shared/hooks/stores/useAuthStore';
import { useServicesStore } from 'shared/hooks/stores/useServicesStore';
import { useAuth } from 'shared/hooks/useAuth';
import { useModal } from 'shared/hooks/useModal';
import { useNotification } from 'shared/hooks/useNotification';
import { useSitemap } from 'shared/hooks/useSitemap';
import { FeedbackModal } from './components/FeedbackModal';
import { useVisitsTable } from './useVisitsTable';

export type VisitsFilters = {
  status: string;
  patientName: string;
  service: string;
  specialist: string;
  dateFrom: string;
  dateTo: string;
  facilityId: string;
};

interface VisitTableProps {
  visits: VisitWithDetails[];
  onCancel: (id: number) => void;
  patientId?: number;
  patientPesel?: string;
  doctorId?: number;
  loading?: boolean;
  refetchVisits: () => Promise<void>;
  isArchivalVisitsOn: boolean;
  onArchivalModeToggle: () => void;
  displayPatientColumn: boolean;
  displayDoctorColumn: boolean;
  disableFacilityFilter?: boolean;
}

const columns = (
  onCancel: (id: number) => void,
  onNavigateToVisit: (id: number) => void,
  onNavigateToPatient: (id: number) => void,
  displayPatientColumn: boolean,
  displayDoctorColumn: boolean,
  isPatient: boolean,
  isDoctor: boolean,
  isWardClerk: boolean,
  onViewReview?: (id: number, rating: number, message?: string) => void,
  onAddReview?: (id: number) => void,
  loading?: boolean,
): GridColDef<VisitWithDetails>[] => [
  {
    field: 'index',
    headerName: '#',
    width: 70,
    valueGetter: (_value, row, _, apiRef) =>
      apiRef.current.getRowIndexRelativeToVisibleRows(row.id) + 1,
  },
  ...(displayPatientColumn
    ? [
        {
          field: 'patientName',
          headerName: 'Pacjent',
          minWidth: 200,
          flex: 1,
          valueGetter: (_: undefined, row: VisitWithDetails) => row.patientFullName,
          renderCell: ({ row }: { row: VisitWithDetails }) => (
            <Link
              component="button"
              onClick={() => onNavigateToPatient(row.patientId)}
              underline="hover"
              color="primary"
              sx={{ cursor: 'pointer', fontWeight: 500 }}
            >
              {row.patientFullName}
            </Link>
          ),
        },
      ]
    : []),
  {
    field: 'date',
    headerName: 'Data',
    flex: 1,
    valueGetter: (_, row) => {
      if (row.status === 'CANCELLED') {
        return 'Anulowana';
      }
      return row.date ? new Date(row.date) : null;
    },
    valueFormatter: (_, row) => {
      if (row.status === 'CANCELLED') {
        return 'Anulowana';
      }
      return row.date ? format(new Date(row.date), 'dd.MM.yyyy') : 'Brak daty';
    },
    sortComparator: (v1, v2) => {
      if (!v1 && !v2) return 0;
      if (!v1) return 1;
      if (!v2) return -1;

      if (v1 === 'Anulowana' && v2 === 'Anulowana') return 0;
      if (v1 === 'Anulowana') return 1;
      if (v2 === 'Anulowana') return -1;

      return v1.getTime() - v2.getTime();
    },
  },
  {
    field: 'status',
    headerName: 'Status',
    flex: 1,
    renderCell: (params) => {
      const { label, color } = getVisitStatusLabel(params.row.status);
      return <span style={{ color }}>{label}</span>;
    },
  },
  {
    field: 'hour',
    headerName: 'Godzina',
    minWidth: 200,
    flex: 1,
    valueGetter: (_, row) => {
      if (row.status === 'CANCELLED') {
        return 'Anulowana';
      }

      if (row.startTime && row.endTime && row.date) {
        const date = new Date(row.date);
        const parseTime = (timeStr: string) => parse(timeStr, 'HH:mm:ss', date);
        const formatTime = (timeStr: string) => format(parseTime(timeStr), 'HH:mm');
        return `${formatTime(row.startTime)} - ${formatTime(row.endTime)}`;
      }

      return 'Brak godziny';
    },
  },
  {
    field: 'service',
    headerName: 'Usługa',
    minWidth: 200,
    flex: 1,
    valueGetter: (_, row) => row.serviceName,
  },
  ...(displayDoctorColumn
    ? [
        {
          field: 'specialist',
          headerName: 'Specjalista',
          minWidth: 200,
          flex: 1,
          valueGetter: (_: undefined, row: VisitWithDetails) => row.doctorFullName,
        },
      ]
    : []),

  {
    field: 'actions',
    headerName: 'Akcje',
    type: 'actions',
    width: 70,
    flex: 0.5,
    getActions: (params: { row: VisitWithDetails }) => {
      if (params.row.status === 'CANCELLED') {
        return [];
      }

      const actions = [];

      if ((isWardClerk || isPatient) && params.row.status === VisitWithDetailsStatus.PLANNED) {
        actions.push(
          <GridActionsCellItem
            key={`cancel-${params.row.id}`}
            label="Anuluj wizytę"
            disabled={loading}
            onClick={() => onCancel(params.row.id)}
            showInMenu
            sx={{ color: 'error.main' }}
          />,
        );
      }

      if ((isPatient && params.row.status === VisitWithDetailsStatus.CLOSED) || isDoctor) {
        actions.push(
          <GridActionsCellItem
            key={`begin-${params.row.id}`}
            label="Przejdź do wizyty"
            onClick={() => onNavigateToVisit(params.row.id)}
            showInMenu
          />,
        );
      }

      if (
        params.row.status === VisitWithDetailsStatus.CLOSED &&
        isPatient &&
        !params.row.feedbackRating
      ) {
        actions.push(
          <GridActionsCellItem
            key={`add-review-${params.row.id}`}
            label="Dodaj opinię"
            onClick={() => onAddReview?.(params.row.id)}
            showInMenu
          />,
        );
      }
      if (params.row.status === VisitWithDetailsStatus.CLOSED && params.row.feedbackRating) {
        actions.push(
          <GridActionsCellItem
            key={`view-review-${params.row.id}`}
            label="Zobacz opinię"
            onClick={() =>
              onViewReview?.(
                params.row.id,
                params.row.feedbackRating ?? 0,
                params.row.feedbackMessage,
              )
            }
            showInMenu
          />,
        );
      }
      return actions;
    },
  },
];

export const VisitsTable: FC<VisitTableProps> = ({
  visits,
  onCancel,
  loading,
  refetchVisits,
  isArchivalVisitsOn,
  onArchivalModeToggle,
  displayPatientColumn,
  displayDoctorColumn,
  disableFacilityFilter = false,
}) => {
  const currentFacilityId = useAuthStore((state) => state.user?.facilityId);
  const services = useServicesStore((state) => state.regularServices);

  const { isPatient, isDoctor, isWardClerk } = useAuth();
  const navigate = useNavigate();
  const sitemap = useSitemap();
  const { showNotification, NotificationComponent } = useNotification();

  const [filters, setFilters] = useState<VisitsFilters>({
    status: VisitWithDetailsStatus.PLANNED,
    patientName: '',
    service: '',
    specialist: '',
    dateFrom: '',
    dateTo: '',
    facilityId: currentFacilityId ? currentFacilityId.toString() : '',
  });

  const { visitsFilterConfig, filteredVisits } = useVisitsTable({
    visits,
    filters,
    services,
    displayPatientColumn,
    displayDoctorColumn,
    disableFacilityFilter,
  });

  const handleFilterChange = useCallback((name: keyof VisitsFilters, value: string) => {
    setFilters((prev) => ({ ...prev, [name]: value }));
  }, []);

  const setFilterDateToToday = useCallback(() => {
    const todayStart = format(startOfDay(new Date()), appConfig.dateTimeFormat);
    const todayEnd = format(endOfDay(new Date()), appConfig.dateTimeFormat);

    handleFilterChange('dateFrom', todayStart);
    handleFilterChange('dateTo', todayEnd);
  }, [handleFilterChange]);

  const { openModal, closeModal } = useModal();
  const handleViewReview = useCallback(
    (visitId: number, rating: number, message?: string) => {
      openModal(
        'reviewModal',
        <FeedbackModal
          visitId={visitId}
          onCancel={() => closeModal('reviewModal')}
          existingValues={{ rating, message }}
          disabled
          title="Twoja opinia"
        />,
      );
    },
    [openModal, closeModal],
  );

  const handleAddReview = useCallback(
    (visitId: number) => {
      openModal(
        'reviewModal',
        <FeedbackModal
          visitId={visitId}
          onCancel={() => closeModal('reviewModal')}
          title="Wystaw opinię"
          onSubmitSuccess={() => {
            refetchVisits();
            showNotification('Dziękujemy za opinię!', 'success');
          }}
          onSubmitError={() => {
            showNotification('Coś poszło nie tak przy dodawaniu opinii...', 'error');
          }}
        />,
      );
    },
    [openModal],
  );

  const onNavigateToVisit = useCallback(
    (id: number) => {
      navigate(sitemap.visit(id));
    },
    [navigate],
  );

  const onNavigateToPatient = useCallback(
    (id: number) => {
      navigate(sitemap.patient(id));
    },
    [navigate],
  );

  const resetFilters = useCallback(() => {
    setFilters((prev) => ({
      status: '',
      patientName: '',
      service: '',
      specialist: '',
      dateFrom: '',
      dateTo: '',
      facilityId: disableFacilityFilter ? prev.facilityId : '',
    }));
  }, [disableFacilityFilter]);

  return (
    <Box sx={{ height: '100%', width: '100%', display: 'flex', flexDirection: 'column', p: 2 }}>
      <Box sx={{ paddingBottom: 2, display: 'flex', gap: 4 }}>
        <Button variant="contained" onClick={setFilterDateToToday}>
          Pokaż dzisiejsze
        </Button>
        <FormControlLabel
          control={
            <Switch checked={isArchivalVisitsOn} onChange={onArchivalModeToggle} color="primary" />
          }
          label="Pokaż archiwalne wizyty (starsze niż 3 miesiące)"
        />
      </Box>
      <TableFilters<VisitsFilters>
        filters={filters}
        filterConfig={visitsFilterConfig}
        onFilterChange={handleFilterChange}
        onReset={resetFilters}
      />
      <Paper sx={{ flexGrow: 1 }}>
        <DataGrid
          getRowClassName={(params) => (params.row.status === 'CANCELLED' ? 'cancelled-visit' : '')}
          rows={filteredVisits}
          columns={columns(
            onCancel,
            onNavigateToVisit,
            onNavigateToPatient,
            displayPatientColumn,
            displayDoctorColumn,
            isPatient,
            isDoctor,
            isWardClerk,
            handleViewReview,
            handleAddReview,
            loading,
          )}
          initialState={{
            pagination: {
              paginationModel: { page: 0, pageSize: 10 },
            },
            sorting: {
              sortModel: [
                { field: 'date', sort: 'asc' },
                { field: 'hour', sort: 'asc' },
              ],
            },
          }}
          pageSizeOptions={[5, 10, 25]}
          paginationMode="client"
          rowHeight={32}
          disableColumnFilter
          disableRowSelectionOnClick
          sx={{
            '& .cancelled-visit': {
              backgroundColor: '#ffe6e6',
            },
          }}
        />
      </Paper>
      <NotificationComponent />
    </Box>
  );
};

export default VisitsTable;
