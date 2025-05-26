import { Box, Button, Paper } from '@mui/material';
import { DataGrid, GridActionsCellItem, GridColDef } from '@mui/x-data-grid';

import { endOfDay, format, startOfDay } from 'date-fns';
import { FC, useCallback, useState } from 'react';
import { Service, VisitDTO } from 'shared/api/generated/generated.schemas';
import { appConfig } from 'shared/appConfig';
import { ReviewModal } from 'shared/components/ReviewModal';
import { TableFilters } from 'shared/components/TableFilters';
import { useAuth } from 'shared/hooks/useAuth';
import { useModal } from 'shared/hooks/useModal';
import { useVisitsTable } from './useVisitsTable';

export type VisitsFilters = {
  status: string;
  patientName: string;
  service: string;
  specialist: string;
  dateFrom: string;
  dateTo: string;
};

interface VisitTableProps {
  visits: VisitDTO[];
  allServices: Service[];
  onEdit?: (id: number) => void;
  onCancel: (id: number) => void;
  loading?: boolean;
}

const columns = (
  onCancel: (id: number) => void,
  onEdit?: (id: number) => void,
  onAddReview?: (id: number, doctorFullName: string) => void,
  showReviewOption?: boolean,
  loading?: boolean,
): GridColDef<VisitDTO>[] => [
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
    valueGetter: (_, row) => `${row.patientFullName}`,
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
      if (row.status === 'CANCELLED') return 'Anulowana';
      return row.startTime ? row.startTime : 'Brak godziny';
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
    getActions: (params: { row: VisitDTO }) => {
      if (params.row.status === 'CANCELLED') return [];

      return [
        <GridActionsCellItem
          key={`begin-${params.row.id}`}
          label="Rozpocznij wizytę"
          onClick={() => onEdit?.(params.row.id)}
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
  onEdit,
  onCancel,
  loading,
}) => {
  const [filters, setFilters] = useState<VisitsFilters>({
    status: '',
    patientName: '',
    service: '',
    specialist: '',
    dateFrom: '',
    dateTo: '',
  });

  const { isPatient } = useAuth();
  const { visitsFilterConfig, filteredVisits } = useVisitsTable({ visits, filters, allServices });

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
  const handleAddReviewClick = useCallback(
    (visitId: number, specialistFullName: string) => {
      openModal(
        'reviewModal',
        <ReviewModal
          visitId={visitId.toString()}
          specialistFullName={specialistFullName}
          onConfirm={(rating, visitId, additionalInfo) => {
            console.log('Review Submitted:', { rating, visitId, additionalInfo });
            closeModal('reviewModal');
          }}
          onCancel={() => closeModal('reviewModal')}
        />,
      );
    },
    [openModal, closeModal],
  );

  const resetFilters = useCallback(() => {
    setFilters({
      status: '',
      patientName: '',
      service: '',
      specialist: '',
      dateFrom: '',
      dateTo: '',
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
          columns={columns(onCancel, onEdit, handleAddReviewClick, isPatient, loading)}
          initialState={{
            pagination: {
              paginationModel: { page: 0, pageSize: 10 },
            },
          }}
          pageSizeOptions={[5, 10, 25]}
          paginationMode="client"
          rowHeight={32}
          disableColumnFilter
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
