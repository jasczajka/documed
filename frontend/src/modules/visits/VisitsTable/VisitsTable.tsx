import { Box, Paper } from '@mui/material';
import { DataGrid, GridActionsCellItem, GridColDef } from '@mui/x-data-grid';

import { format } from 'date-fns';
import { FC, useEffect, useState } from 'react';
import { FilterConfig, TableFilters } from 'shared/components/TableFilters';
import { VisitLite } from 'shared/types/Visit';

type VisitsFilters = {
  patientName: string;
  date: string;
  service: string;
  specialist: string;
};

interface VisitTableProps {
  visits: VisitLite[];
  onEdit?: (id: number) => void;
  onDelete?: (id: number) => void;
}

const visitsFilterConfig: FilterConfig[] = [
  {
    name: 'patientName',
    label: 'Patient',
    type: 'text',
    width: 200,
  },
  {
    name: 'date',
    label: 'Date',
    type: 'date',
    width: 200,
  },
  {
    name: 'service',
    label: 'Service',
    type: 'select',
    options: [
      { value: '', label: 'All' },
      { value: 'Kardiologia', label: 'Kardiologia' },
      { value: 'Stomatologia', label: 'Stomatologia' },
    ],
    width: 200,
  },
  {
    name: 'specialist',
    label: 'Specialist',
    type: 'text',
    width: 200,
  },
];

const columns = (
  onEdit?: (id: number) => void,
  onDelete?: (id: number) => void,
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
      return row.timeSlots[0]?.date ? format(new Date(row.timeSlots[0].date), 'PP') : 'Brak daty';
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
  // {
  //   field: 'service',
  //   headerName: 'Usługa',
  //   minWidth: 200,
  //   flex: 1,
  //   valueGetter: (_, row) => 'Kardiologia',
  //   // || 'Brak usługi',
  // },
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
    ],
  },
];

export const VisitsTable: FC<VisitTableProps> = ({ visits, onEdit, onDelete }) => {
  const [filteredVisits, setFilteredVisits] = useState<VisitLite[]>(visits);
  const [filters, setFilters] = useState<VisitsFilters>({
    patientName: '',
    date: '',
    service: '',
    specialist: '',
  });

  useEffect(() => {
    const filtered = visits.filter((visit) => {
      const patientFullName = `${visit.patient.firstName} ${visit.patient.lastName}`.toLowerCase();
      const visitDate = visit.timeSlots[0]?.date
        ? format(new Date(visit.timeSlots[0].date), 'yyyy-MM-dd')
        : '';
      const doctorFullName = visit.doctor
        ? `${visit.doctor.firstName} ${visit.doctor.lastName}`.toLowerCase()
        : '';

      return (
        patientFullName.includes(filters.patientName.toLowerCase()) &&
        visitDate.includes(filters.date) &&
        'Kardiologia'.toLowerCase().includes(filters.service.toLowerCase()) &&
        doctorFullName.includes(filters.specialist.toLowerCase())
      );
    });

    setFilteredVisits(filtered);
  }, [visits, filters]);

  const handleFilterChange = (name: keyof VisitsFilters, value: string) => {
    setFilters((prev) => ({ ...prev, [name]: value }));
  };

  const resetFilters = () => {
    setFilters({
      patientName: '',
      date: '',
      service: '',
      specialist: '',
    });
  };

  return (
    <Paper sx={{ height: '100%', width: '100%', display: 'flex', flexDirection: 'column', p: 2 }}>
      <TableFilters<VisitsFilters>
        filters={filters}
        filterConfig={visitsFilterConfig}
        onFilterChange={handleFilterChange}
        onReset={resetFilters}
        resultsCount={filteredVisits.length}
      />
      <Box sx={{ flexGrow: 1 }}>
        <DataGrid
          rows={filteredVisits}
          columns={columns(onEdit, onDelete)}
          initialState={{
            pagination: {
              paginationModel: { page: 0, pageSize: 10 },
            },
          }}
          pageSizeOptions={[5, 10, 25]}
          disableRowSelectionOnClick
          sx={{
            border: 0,
          }}
        />
      </Box>
    </Paper>
  );
};

export default VisitsTable;
