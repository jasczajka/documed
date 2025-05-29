import { yupResolver } from '@hookform/resolvers/yup';
import { Button, Dialog, DialogActions, DialogTitle, Stack, TextField } from '@mui/material';
import { FC } from 'react';
import { Controller, useForm } from 'react-hook-form';
import { MedicineSearch } from 'shared/components/MedicineSearch/MedicineSearch';
import * as Yup from 'yup';

interface FormData {
  medicine: {
    id: string;
    name: string;
    commonName?: string;
    dosage?: string;
  };
  amount: number;
}

const validationSchema = Yup.object().shape({
  medicine: Yup.object()
    .shape({
      id: Yup.string().required(),
      name: Yup.string().required(),
    })
    .required('Wybierz lek'),
  amount: Yup.number()
    .typeError('Wprowadź liczbę')
    .required('Wybierz liczbę opakowań')
    .positive('Liczba musi być większa od zera')
    .integer('Musi być liczbą całkowitą'),
});

interface AddMedicineToPrescriptionModalProps {
  onSubmitForm: (data: FormData) => void;
  onCancel: () => void;
}

export const AddMedicineToPrescriptionModal: FC<AddMedicineToPrescriptionModalProps> = ({
  onSubmitForm,
  onCancel,
}) => {
  const {
    control,
    handleSubmit,
    formState: { errors },
    setValue,
  } = useForm<FormData>({
    resolver: yupResolver(validationSchema),
  });

  const onSubmit = (data: FormData) => {
    onSubmitForm({
      amount: data.amount,
      medicine: {
        id: data.medicine.id,
        name: data.medicine.name,
        commonName: data.medicine.commonName,
        dosage: data.medicine.dosage,
      },
    });
  };

  return (
    <Dialog
      open
      onClose={onCancel}
      slotProps={{
        paper: {
          sx: {
            p: 3,
            borderRadius: 3,
            minWidth: 500,
          },
        },
      }}
      component="form"
      onSubmit={handleSubmit(onSubmit)}
    >
      <DialogTitle>Dodaj lek do recepty</DialogTitle>
      <Stack direction="column" spacing={8} width="100%">
        <MedicineSearch
          onChange={(medicine) => {
            if (medicine) {
              setValue('medicine', medicine);
            }
          }}
        />
        <Controller
          name="amount"
          control={control}
          render={({ field }) => (
            <TextField
              {...field}
              label="Liczba opakowań"
              type="number"
              error={!!errors.amount}
              helperText={errors.amount?.message}
              slotProps={{ htmlInput: { min: 1 } }}
              fullWidth
            />
          )}
        />
      </Stack>
      <DialogActions sx={{ mt: 2, justifyContent: 'flex-end', gap: 1 }}>
        <Button onClick={onCancel} color="error" variant="outlined" sx={{ minWidth: 100 }}>
          Anuluj
        </Button>
        <Button type="submit" color="success" variant="contained" sx={{ minWidth: 100 }}>
          Potwierdź
        </Button>
      </DialogActions>
    </Dialog>
  );
};
