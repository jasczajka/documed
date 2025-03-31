import { Box, Button, Paper } from '@mui/material';
import { DataGrid, GridActionsCellItem, GridColDef } from '@mui/x-data-grid';

import { endOfDay, format, startOfDay } from 'date-fns';
import { FC, useCallback, useState } from 'react';
import { appConfig } from 'shared/appConfig';
import { ReviewModal } from 'shared/components/ReviewModal';
import { TableFilters } from 'shared/components/TableFilters';
import { useModal } from 'shared/hooks/useModal';
import { VisitLite } from 'shared/types/Visit';
import { useVisitsTable } from './useVisitsTable';

export type VisitsFilters = {
  serviceType: string;
  patientName: string;
  service: string;
  specialist: string;
  dateFrom: string;
  dateTo: string;
};

interface VisitTableProps {
  visits: VisitLite[];
  onEdit?: (id: number) => void;
  onDelete?: (id: number) => void;
}

const columns = (
  onEdit?: (id: number) => void,
  onDelete?: (id: number) => void,
  onAddReview?: (id: number, doctorFullName: string) => void,
  showReviewOption?: boolean,
): GridColDef<VisitLite>[] => [
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
    valueGetter: (_, row) => `${row.patient.firstName} ${row.patient.lastName}`,
  },
  {
    field: 'date',
    headerName: 'Data',
    minWidth: 200,
    flex: 1,
    valueGetter: (_, row) => {
      return row.timeSlots[0]?.date
        ? format(new Date(row.timeSlots[0].date), 'dd.MM.yyyy')
        : 'Brak daty';
    },
  },
  {
    field: 'hour',
    headerName: 'Godzina',
    minWidth: 200,
    flex: 1,
    valueGetter: (_, row) => {
      return row.timeSlots[0].startTime ? `${row.timeSlots[0].startTime}` : 'Brak godziny';
    },
  },
  // @TODO to be finished when we define types for services
  {
    field: 'service',
    headerName: 'Usługa',
    minWidth: 200,
    flex: 1,
    // valueGetter: (_, row) => 'Kardiologia',
    valueGetter: () => 'Kardiologia',
    // || 'Brak usługi',
  },
  {
    field: 'specialist',
    headerName: 'Specjalista',
    minWidth: 200,
    flex: 1,
    valueGetter: (_, row) =>
      row.doctor ? `${row.doctor.firstName} ${row.doctor.lastName}` : 'Brak specjalisty',
  },
  {
    field: 'actions',
    headerName: 'Akcje',
    type: 'actions',
    width: 70,
    flex: 0.5,
    getActions: (params: { row: VisitLite }) => [
      <GridActionsCellItem
        key={`begin-${params.row.id}`}
        label="Rozpocznij wizytę"
        onClick={() => onEdit?.(params.row.id)}
        showInMenu
      />,
      <GridActionsCellItem
        key={`cancel-${params.row.id}`}
        label="Anuluj wizytę"
        onClick={() => onDelete?.(params.row.id)}
        showInMenu
        sx={{ color: 'error.main' }}
      />,
      ...(showReviewOption && !params.row.feedbackRating
        ? [
            <GridActionsCellItem
              key={`review-${params.row.id}`}
              label="Dodaj opinię"
              onClick={() =>
                onAddReview?.(
                  params.row.id,
                  `${params.row.doctor?.firstName && params.row.doctor?.lastName ? `${params.row.doctor?.firstName} ${params.row.doctor?.lastName}` : 'Nieznany specjalista'}`,
                )
              }
              showInMenu
            />,
          ]
        : []),
    ],
  },
];

export const VisitsTable: FC<VisitTableProps> = ({ visits, onEdit, onDelete }) => {
  const [filters, setFilters] = useState<VisitsFilters>({
    serviceType: '',
    patientName: '',
    service: '',
    specialist: '',
    dateFrom: '',
    dateTo: '',
  });

  // @TODO replace with useRoles logic when they're done
  const isPatient = true;
  const { visitsFilterConfig, filteredVisits } = useVisitsTable({ visits, filters });

  const handleFilterChange = useCallback((name: keyof VisitsFilters, value: string) => {
    console.log(value);
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
      console.log('Modal should be open now');
    },
    [openModal, closeModal],
  );

  const resetFilters = useCallback(() => {
    setFilters({
      serviceType: '',
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
          rows={filteredVisits}
          columns={columns(onEdit, onDelete, handleAddReviewClick, isPatient)}
          initialState={{
            pagination: {
              paginationModel: { page: 0, pageSize: 10 },
            },
          }}
          pageSizeOptions={[5, 10, 25]}
          paginationMode="client"
          rowHeight={32}
          disableColumnFilter
        />
      </Paper>
    </Box>
  );
};

export default VisitsTable;
