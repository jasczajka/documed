import { Box, Button, Link, Paper } from '@mui/material';
import { DataGrid, GridActionsCellItem, GridColDef } from '@mui/x-data-grid';

import { endOfDay, format, startOfDay } from 'date-fns';
import { FC, useCallback, useState } from 'react';
import { useNavigate } from 'react-router';
import {
  AdditionalServiceReturnDTO,
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
  additionalServices: AdditionalServiceReturnDTO[];
  allAdditionalServices: Service[];
  refetch: () => Promise<void>;
  patientId?: number;
  doctorId?: number;
  loading?: boolean;
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
  ) => void,
  onNavigateToPatient: (id: number) => void,
): GridColDef<AdditionalServiceReturnDTO>[] => [
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
    renderCell: ({ row }) => (
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
    getActions: (params: { row: AdditionalServiceReturnDTO }) => {
      return [
        <GridActionsCellItem
          key={`begin-${params.row.id}`}
          label="Wyświetl szczegóły"
          onClick={() =>
            onEdit(params.row.fulfillerId, params.row.patientId, params.row.patientFullName, 12, {
              id: params.row.id,
              serviceId: params.row.serviceId,
              existingAttachments: params.row.attachments,
              description: params.row.description,
            })
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
}) => {
  const { isPatient } = useAuth();
  const navigate = useNavigate();
  const sitemap = useSitemap();
  const { openModal, closeModal } = useModal();
  const { showNotification, NotificationComponent } = useNotification();
  const [filters, setFilters] = useState<AdditionalServiceFilters>({
    patientName: '',
    service: '',
    fulfiller: '',
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
    ) => {
      openModal(
        'editAdditionalServiceModal',
        <AdditionalServiceModal
          allAdditionalServices={allAdditionalServices}
          patientId={patientId}
          fulfillerId={fulfillerId}
          patientFullName={patientFullName}
          patientAge={patientAge}
          onConfirm={async () => {
            await refetch();
            closeModal('editAdditionalServiceModal');
            showNotification('Zaktualizowano dane usługi dodatkowej', 'success');
          }}
          onCancel={() => closeModal('editAdditionalServiceModal')}
          mode="edit"
          existingServiceData={existingServiceData}
          refetch={refetch}
        />,
      );
    },
    [openModal, closeModal, allAdditionalServices],
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
      <Box sx={{ paddingBottom: 2 }}>
        <Button variant="contained" onClick={setFilterDateToToday}>
          Pokaż dzisiejsze
        </Button>
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
          columns={columns(handleEditClick, onNavigateToPatient)}
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
      <NotificationComponent />
    </Box>
  );
};

export default AdditionalServicesTable;
