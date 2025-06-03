import { Box, Button, Link, Paper, Typography } from '@mui/material';
import { DataGrid, GridActionsCellItem, GridColDef } from '@mui/x-data-grid';

import { endOfDay, format, parse, startOfDay } from 'date-fns';
import { FC, useCallback, useState } from 'react';
import { useNavigate } from 'react-router';
import {
  Service,
  VisitWithDetails,
  VisitWithDetailsStatus,
} from 'shared/api/generated/generated.schemas';
import { appConfig } from 'shared/appConfig';
import { ReviewModal } from 'shared/components/ReviewModal';
import { TableFilters } from 'shared/components/TableFilters';
import { useAuthStore } from 'shared/hooks/stores/useAuthStore';
import { useAuth } from 'shared/hooks/useAuth';
import { useModal } from 'shared/hooks/useModal';
import { useSitemap } from 'shared/hooks/useSitemap';
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
  allServices: Service[];
  onCancel: (id: number) => void;
  patientId?: number;
  patientPesel?: string;
  doctorId?: number;
  loading?: boolean;
}

const columns = (
  onCancel: (id: number) => void,
  onNavigateToVisit: (id: number) => void,
  onNavigateToPatient: (id: number) => void,
  isPatient: boolean,
  onAddReview?: (id: number, doctorFullName: string) => void,
  showReviewOption?: boolean,
  loading?: boolean,
): GridColDef<VisitWithDetails>[] => [
  {
    field: 'index',
    headerName: '#',
    width: 70,
    valueGetter: (_value, row, _, apiRef) =>
      apiRef.current.getRowIndexRelativeToVisibleRows(row.id) + 1,
  },
  {
    field: 'patientName',
    headerName: 'Pacjent',
    minWidth: 200,
    flex: 1,
    renderCell: ({ row }) =>
      isPatient ? (
        <Typography>{row.patientFullName}</Typography>
      ) : (
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
  {
    field: 'date',
    headerName: 'Data',
    minWidth: 200,
    flex: 1,
    valueGetter: (_, row) => {
      if (row.status === 'CANCELLED') return 'Anulowana';
      return row.date ? format(new Date(row.date), 'dd.MM.yyyy') : 'Brak daty';
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
  {
    field: 'specialist',
    headerName: 'Specjalista',
    minWidth: 200,
    flex: 1,
    valueGetter: (_, row) => row.doctorFullName,
  },
  {
    field: 'actions',
    headerName: 'Akcje',
    type: 'actions',
    width: 70,
    flex: 0.5,
    getActions: (params: { row: VisitWithDetails }) => {
      if (params.row.status === 'CANCELLED') return [];

      return [
        <GridActionsCellItem
          key={`begin-${params.row.id}`}
          label="Przejdź do wizyty"
          onClick={() => onNavigateToVisit(params.row.id)}
          showInMenu
        />,
        <GridActionsCellItem
          key={`cancel-${params.row.id}`}
          label="Anuluj wizytę"
          disabled={loading}
          onClick={() => onCancel(params.row.id)}
          showInMenu
          sx={{ color: 'error.main' }}
        />,
        ...(showReviewOption
          ? [
              <GridActionsCellItem
                key={`review-${params.row.id}`}
                label="Dodaj opinię"
                onClick={() => onAddReview?.(params.row.id, params.row.doctorFullName)}
                showInMenu
              />,
            ]
          : []),
      ];
    },
  },
];

export const VisitsTable: FC<VisitTableProps> = ({
  visits,
  allServices,
  onCancel,
  loading,
  patientId,
  doctorId,
}) => {
  const currentFacilityId = useAuthStore((state) => state.user?.facilityId);
  const [filters, setFilters] = useState<VisitsFilters>({
    status: VisitWithDetailsStatus.PLANNED,
    patientName: '',
    service: '',
    specialist: '',
    dateFrom: '',
    dateTo: '',
    facilityId: currentFacilityId ? currentFacilityId.toString() : '',
  });

  const { isPatient } = useAuth();
  const navigate = useNavigate();
  const sitemap = useSitemap();
  const { visitsFilterConfig, filteredVisits } = useVisitsTable({
    visits,
    filters,
    allServices,
    isPatient,
    patientId,
    doctorId,
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

  const { openModal } = useModal();

  const handleAddReviewClick = useCallback(
    (visitId: number, specialistFullName: string) => {
      openModal('reviewModal', (close) => (
        <ReviewModal
          visitId={visitId.toString()}
          specialistFullName={specialistFullName}
          onConfirm={(rating, visitId, additionalInfo) => {
            console.log('Review Submitted:', { rating, visitId, additionalInfo });
            close();
          }}
          onCancel={close}
        />
      ));
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
    setFilters({
      status: '',
      patientName: '',
      service: '',
      specialist: '',
      dateFrom: '',
      dateTo: '',
      facilityId: '',
    });
  }, []);

  return (
    <Box sx={{ height: '100%', width: '100%', display: 'flex', flexDirection: 'column', p: 2 }}>
      <Box sx={{ paddingBottom: 2 }}>
        <Button variant="contained" onClick={setFilterDateToToday}>
          Pokaż dzisiejsze
        </Button>
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
            isPatient,
            handleAddReviewClick,
            isPatient,
            loading,
          )}
          initialState={{
            pagination: {
              paginationModel: { page: 0, pageSize: 10 },
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
    </Box>
  );
};

export default VisitsTable;
