import { yupResolver } from '@hookform/resolvers/yup';
import { Delete } from '@mui/icons-material';
import { Box, Button, Card, IconButton, TextField } from '@mui/material';
import { DataGrid, GridColDef } from '@mui/x-data-grid';
import { areIntervalsOverlapping, format, isAfter, parseISO } from 'date-fns';
import dayjs from 'dayjs';
import { FC } from 'react';
import { Controller, useForm } from 'react-hook-form';
import { FreeDaysReturnDTO } from 'shared/api/generated/generated.schemas';
import { cancelFreeDays } from 'shared/api/generated/time-slot-controller/time-slot-controller';
import { appConfig } from 'shared/appConfig';
import ConfirmationModal from 'shared/components/ConfirmationModal/ConfirmationModal';
import { useModal } from 'shared/hooks/useModal';
import { useNotification } from 'shared/hooks/useNotification';
import * as Yup from 'yup';

type FormData = {
  startDate: string;
  endDate: string;
};

interface FreeDaysTabProps {
  currentFreeDays: FreeDaysReturnDTO[];
  onSubmitForm: (data: FormData) => void;
  onSuccessfulEdit: () => Promise<void>;
  loading?: boolean;
  disabled?: boolean;
}

const validationSchema = Yup.object({
  startDate: Yup.string().required('Data początkowa jest wymagana'),
  endDate: Yup.string()
    .required('Data końcowa jest wymagana')
    .test(
      'is-after-start',
      'Data końcowa nie może być wcześniejsza niż początkowa',
      function (value) {
        const { startDate } = this.parent;
        return isAfter(parseISO(value), parseISO(startDate)) || value === startDate;
      },
    ),
});

export const FreeDaysTab: FC<FreeDaysTabProps> = ({
  currentFreeDays,
  onSubmitForm,
  onSuccessfulEdit,
  loading,
  disabled = false,
}) => {
  const { openModal } = useModal();
  const { showNotification, NotificationComponent } = useNotification();

  const {
    control,
    handleSubmit,
    formState: { errors },
    reset,
  } = useForm<FormData>({
    resolver: yupResolver(validationSchema),
    defaultValues: {
      startDate: '',
      endDate: '',
    },
  });

  const onSubmit = async (data: FormData) => {
    const newRange = {
      start: new Date(data.startDate),
      end: new Date(data.endDate),
    };

    const overlaps = currentFreeDays.some((freeDays) =>
      areIntervalsOverlapping(
        {
          start: new Date(freeDays.startDate),
          end: new Date(freeDays.endDate),
        },
        newRange,
      ),
    );

    if (overlaps) {
      showNotification('Nowy urlop nachodzi na inny istniejący urlop.', 'error');
      return;
    }

    openModal('confirmNewFreeDaysModal', (close) => (
      <ConfirmationModal
        message="Pamiętaj, że dodanie urlopu dla lekarza spowoduje anulowanie jego wizyt w tym czasie!"
        onConfirm={async () => {
          onSubmitForm(data);
          reset();
          close();
        }}
        onCancel={close}
      />
    ));
  };

  const handleDelete = async (row: FreeDaysReturnDTO) => {
    await cancelFreeDays(row.id);
    await onSuccessfulEdit();
  };

  const columns: GridColDef<FreeDaysReturnDTO>[] = [
    {
      field: 'startDate',
      headerName: 'Data początkowa',
      flex: 1,
      valueGetter: (_, row) => format(parseISO(row.startDate), appConfig.localDateFormat),
    },
    {
      field: 'endDate',
      headerName: 'Data końcowa',
      flex: 1,
      valueGetter: (_, row) => format(parseISO(row.endDate), appConfig.localDateFormat),
    },
    {
      field: 'actions',
      headerName: 'Akcje',
      flex: 0.5,
      renderCell: ({ row }) =>
        disabled ? (
          <span style={{ color: '#999' }}>Brak akcji</span>
        ) : (
          <IconButton onClick={() => handleDelete(row)}>
            <Delete />
          </IconButton>
        ),
    },
  ];

  return (
    <Box sx={{ display: 'flex', flexDirection: 'column', gap: 4 }}>
      <Box
        component={disabled ? 'div' : 'form'}
        onSubmit={handleSubmit(onSubmit)}
        sx={{ display: 'flex', flexDirection: 'column', gap: 4 }}
      >
        <Box sx={{ display: 'flex', width: '100%', gap: 8 }}>
          <Controller
            name="startDate"
            control={control}
            render={({ field }) => (
              <TextField
                {...field}
                label="Data początkowa"
                type="date"
                slotProps={{
                  htmlInput: {
                    min: dayjs().format('YYYY-MM-DD'),
                  },
                  inputLabel: { shrink: true },
                }}
                error={!!errors.startDate}
                helperText={errors.startDate?.message}
                fullWidth
                disabled={disabled}
              />
            )}
          />
          <Controller
            name="endDate"
            control={control}
            render={({ field }) => (
              <TextField
                {...field}
                label="Data końcowa"
                type="date"
                slotProps={{
                  htmlInput: {
                    min: dayjs().format('YYYY-MM-DD'),
                  },
                  inputLabel: { shrink: true },
                }}
                error={!!errors.endDate}
                helperText={errors.endDate?.message}
                fullWidth
                disabled={disabled}
              />
            )}
          />
          <Button
            type="submit"
            variant="contained"
            color="primary"
            disabled={loading || disabled}
            sx={{ minWidth: '200px', alignSelf: 'flex-start' }}
          >
            Dodaj urlop
          </Button>
        </Box>
      </Box>

      <Box sx={{ mt: 4 }}>
        <Card sx={{ width: '100%' }}>
          <DataGrid
            rows={currentFreeDays}
            columns={columns}
            initialState={{
              pagination: {
                paginationModel: { page: 0, pageSize: 5 },
              },
              sorting: {
                sortModel: [{ field: 'startDate', sort: 'asc' }],
              },
            }}
            pageSizeOptions={[5, 10]}
            disableRowSelectionOnClick
            disableColumnMenu
            hideFooterSelectedRowCount
          />
        </Card>
      </Box>
      <NotificationComponent />
    </Box>
  );
};
