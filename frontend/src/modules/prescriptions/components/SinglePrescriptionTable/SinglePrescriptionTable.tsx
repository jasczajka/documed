import { Delete } from '@mui/icons-material';
import { Box, Button, IconButton, Paper, TextField, Typography } from '@mui/material';
import { DataGrid, GridColDef } from '@mui/x-data-grid';
import dayjs from 'dayjs';
import { FC, useEffect, useMemo, useState } from 'react';
import { MedicineWithAmount } from 'shared/api/generated/generated.schemas';
import { useModal } from 'shared/hooks/useModal';
import { AddMedicineToPrescriptionModal } from '../AddMedicineToPrescriptionModal/AddMedicineToPrescriptionModal';

interface SinglePrescriptionTableProps {
  existingMedicines?: MedicineWithAmount[];
  onAddMedicineToPrescription: (medicineId: string, amount: number) => void;
  onRemoveMedicineFromPrescription: (medicineId: string) => void;
  prescriptionExpirationDate: Date | null;
  handlePrescriptionExpirationDateChange: (newDate: Date) => void;
  disabled?: boolean;
}

export const SinglePrescriptionTable: FC<SinglePrescriptionTableProps> = ({
  existingMedicines,
  onAddMedicineToPrescription,
  onRemoveMedicineFromPrescription,
  prescriptionExpirationDate,
  handlePrescriptionExpirationDateChange,
  disabled = false,
}) => {
  const [medicines, setMedicines] = useState<MedicineWithAmount[]>(existingMedicines ?? []);
  const { openModal } = useModal();

  const handleAddNewMedicineClick: () => void = () => {
    openModal('cancelVisitModal', (close) => (
      <AddMedicineToPrescriptionModal
        onSubmitForm={(data) => {
          const newMedicine: MedicineWithAmount = {
            id: data.medicine.id,
            name: data.medicine.name,
            commonName: data.medicine.commonName || '',
            dosage: data.medicine.dosage || '',
            amount: data.amount,
          };

          setMedicines((prev) => [...prev, newMedicine]);

          onAddMedicineToPrescription(data.medicine.id, data.amount);
          close();
        }}
        onCancel={close}
        getExistingMedicineIds={() => medicines.map((m) => m.id)}
      />
    ));
  };

  const handleRemoveMedicine = (medicineId: string) => {
    setMedicines((prev) => prev.filter((med) => med.id !== medicineId));
    onRemoveMedicineFromPrescription(medicineId);
  };

  useEffect(() => {
    if (existingMedicines) {
      setMedicines(existingMedicines);
    }
  }, [existingMedicines]);

  const columns: GridColDef<MedicineWithAmount>[] = useMemo(
    () => [
      { field: 'name', headerName: 'Nazwa', flex: 1 },
      { field: 'commonName', headerName: 'Nazwa zwyczajowa', flex: 1 },
      { field: 'dosage', headerName: 'Dawkowanie', flex: 1 },
      { field: 'amount', headerName: 'Ilość', type: 'number', flex: 0.5 },
      ...(disabled
        ? []
        : [
            {
              field: 'actions',
              headerName: 'Akcje',
              flex: 0.5,
              renderCell: ({ row }: { row: MedicineWithAmount }) => (
                <IconButton onClick={() => handleRemoveMedicine(row.id)}>
                  <Delete />
                </IconButton>
              ),
            },
          ]),
    ],
    [disabled],
  );
  return (
    <Paper sx={{ height: '100%' }}>
      <Box
        sx={{
          width: '100%',
          p: 2,
          display: 'flex',
          flexDirection: 'column',
          gap: 2,
          alignItems: 'flex-start',
        }}
      >
        <Typography variant="h6" sx={{ mb: 1 }}>
          Recepta
        </Typography>
        <Box sx={{ width: '100%', alignItems: 'flex-start', display: 'flex', gap: 8 }}>
          {!disabled && (
            <Button variant="contained" onClick={handleAddNewMedicineClick}>
              Dodaj lek
            </Button>
          )}
          <TextField
            label="Data ważności"
            type="date"
            value={
              prescriptionExpirationDate
                ? dayjs(prescriptionExpirationDate).format('YYYY-MM-DD')
                : dayjs().format('YYYY-MM-DD')
            }
            onChange={(e) => {
              const newDate = new Date(e.target.value);
              if (!isNaN(newDate.getTime())) {
                handlePrescriptionExpirationDateChange(newDate);
              }
            }}
            slotProps={{
              inputLabel: { shrink: true },
              htmlInput: {
                min: dayjs().format('YYYY-MM-DD'),
                max: dayjs().add(365, 'day').format('YYYY-MM-DD'),
              },
            }}
            disabled={disabled}
          />
        </Box>
        <DataGrid
          rows={medicines}
          columns={columns}
          getRowId={(row) => row.id}
          pageSizeOptions={[5, 10]}
          disableRowSelectionOnClick
          disableColumnFilter
          sx={{
            width: '100%',
          }}
        />
      </Box>
    </Paper>
  );
};
