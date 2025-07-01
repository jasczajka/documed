import { Box, Button, FormControlLabel, Link, Paper, Switch, Typography } from '@mui/material';
import { DataGrid, GridActionsCellItem, GridColDef } from '@mui/x-data-grid';

import { endOfDay, format, startOfDay } from 'date-fns';
import { FC, useCallback, useState } from 'react';
import { useNavigate } from 'react-router';
import {
  AdditionalServiceWithDetails,
  FileInfoDTO,
  Service,
} from 'shared/api/generated/generated.schemas';
import { appConfig } from 'shared/appConfig';
import { AdditionalServiceModal } from 'shared/components/AdditionalServiceModal';
import { TableFilters } from 'shared/components/TableFilters';
import { useAuth } from 'shared/hooks/useAuth';
import { useModal } from 'shared/hooks/useModal';
import { useNotification } from 'shared/hooks/useNotification';
import { useSitemap } from 'shared/hooks/useSitemap';
import { useAdditionalServicesTable } from './useAdditionalServicesTable';

export type AdditionalServiceFilters = {
  patientName: string;
  service: string;
  fulfiller: string;
  dateFrom: string;
  dateTo: string;
};

interface AdditionalServicesTableProps {
  additionalServices: AdditionalServiceWithDetails[];
  allAdditionalServices: Service[];
  refetch: () => Promise<void>;
  patientId?: number;
  doctorId?: number;
  loading?: boolean;
  isArchivalAdditionalServicesOn: boolean;
  onArchivalModeToggle: () => void;
}

const columns = (
  onEdit: (
    fulfillerId: number,
    patientId: number,
    patientFullName: string,
    patientAge: number,
    existingServiceData: {
      id: number;
      serviceId: number;
      existingAttachments: FileInfoDTO[];
      description?: string;
    },
    patientPesel?: string,
  ) => void,
  onNavigateToPatient: (id: number) => void,
  isPatient: boolean,
): GridColDef<AdditionalServiceWithDetails>[] => [
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
    valueGetter: (_, row) => (row.date ? format(new Date(row.date), 'dd.MM.yyyy') : 'Brak daty'),
  },
  {
    field: 'service',
    headerName: 'Usługa',
    minWidth: 200,
    flex: 1,
    valueGetter: (_, row) => row.serviceName,
  },
  {
    field: 'fulfiller',
    headerName: 'Wykonawca',
    minWidth: 200,
    flex: 1,
    valueGetter: (_, row) => row.fulfillerFullName,
  },
  {
    field: 'actions',
    headerName: 'Akcje',
    type: 'actions',
    width: 70,
    flex: 0.5,
    getActions: (params: { row: AdditionalServiceWithDetails }) => {
      return [
        <GridActionsCellItem
          key={`begin-${params.row.id}`}
          label="Wyświetl szczegóły"
          onClick={() =>
            onEdit(
              params.row.fulfillerId,
              params.row.patientId,
              params.row.patientFullName,
              12,
              {
                id: params.row.id,
                serviceId: params.row.serviceId,
                existingAttachments: params.row.attachments,
                description: params.row.description,
              },
              params.row.patientPesel,
            )
          }
          showInMenu
        />,
      ];
    },
  },
];

export const AdditionalServicesTable: FC<AdditionalServicesTableProps> = ({
  additionalServices,
  allAdditionalServices,
  refetch,
  patientId,
  doctorId,
  isArchivalAdditionalServicesOn,
  onArchivalModeToggle,
}) => {
  const { isNurse, isDoctor, isPatient } = useAuth();
  const { user } = useAuth();
  const navigate = useNavigate();
  const sitemap = useSitemap();
  const { openModal } = useModal();
  const { showNotification, NotificationComponent } = useNotification();
  const [filters, setFilters] = useState<AdditionalServiceFilters>({
    patientName: '',
    service: '',
    fulfiller: isNurse || isDoctor ? `${user?.lastName} ${user?.firstName}` : '',
    dateFrom: '',
    dateTo: '',
  });

  const { additionalServicesFilterConfig, filteredAdditionalServices } = useAdditionalServicesTable(
    { additionalServices, filters, allAdditionalServices, isPatient, patientId, doctorId },
  );

  const handleFilterChange = useCallback((name: keyof AdditionalServiceFilters, value: string) => {
    setFilters((prev) => ({ ...prev, [name]: value }));
  }, []);

  const handleEditClick = useCallback(
    async (
      fulfillerId: number,
      patientId: number,
      patientFullName: string,
      patientAge: number,
      existingServiceData: {
        id: number;
        serviceId: number;
        existingAttachments: FileInfoDTO[];
        description?: string;
      },
      patientPesel?: string,
    ) => {
      openModal('editAdditionalServiceModal', (close) => (
        <AdditionalServiceModal
          allAdditionalServices={allAdditionalServices}
          patientId={patientId}
          patientPesel={patientPesel}
          fulfillerId={fulfillerId}
          patientFullName={patientFullName}
          patientAge={patientAge}
          onConfirm={async () => {
            await refetch();
            close();
            showNotification('Zaktualizowano dane usługi dodatkowej', 'success');
          }}
          onCancel={close}
          mode="edit"
          existingServiceData={existingServiceData}
          refetch={refetch}
          readOnly={!(isNurse || isDoctor)}
        />
      ));
    },
    [openModal, allAdditionalServices],
  );

  const setFilterDateToToday = useCallback(() => {
    const todayStart = format(startOfDay(new Date()), appConfig.dateTimeFormat);
    const todayEnd = format(endOfDay(new Date()), appConfig.dateTimeFormat);

    handleFilterChange('dateFrom', todayStart);
    handleFilterChange('dateTo', todayEnd);
  }, [handleFilterChange]);

  const onNavigateToPatient = useCallback(
    (id: number) => {
      navigate(sitemap.patient(id));
    },
    [navigate],
  );

  const resetFilters = useCallback(() => {
    setFilters({
      patientName: '',
      service: '',
      fulfiller: '',
      dateFrom: '',
      dateTo: '',
    });
  }, []);

  return (
    <Box sx={{ height: '100%', width: '100%', display: 'flex', flexDirection: 'column', p: 2 }}>
      <Box sx={{ paddingBottom: 2, display: 'flex', gap: 4 }}>
        <Button variant="contained" onClick={setFilterDateToToday}>
          Pokaż dzisiejsze
        </Button>
        <FormControlLabel
          control={
            <Switch
              checked={isArchivalAdditionalServicesOn}
              onChange={onArchivalModeToggle}
              color="primary"
            />
          }
          label="Pokaż archiwalne usługi dodatkowe"
        />
      </Box>
      <TableFilters<AdditionalServiceFilters>
        filters={filters}
        filterConfig={additionalServicesFilterConfig}
        onFilterChange={handleFilterChange}
        onReset={resetFilters}
      />
      <Paper sx={{ flexGrow: 1 }}>
        <DataGrid
          rows={filteredAdditionalServices}
          columns={columns(handleEditClick, onNavigateToPatient, isPatient)}
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
        />
      </Paper>
      <NotificationComponent />
    </Box>
  );
};

export default AdditionalServicesTable;
