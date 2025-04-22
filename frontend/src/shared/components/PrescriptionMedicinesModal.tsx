import { Card, Dialog, Paper, Stack, Typography } from '@mui/material';
import { FC } from 'react';
import { MedicineWithAmount } from 'shared/api/generated/generated.schemas';

export interface PrescriptionMedicinesModalProps {
  prescriptionId: string;
  medicines: MedicineWithAmount[];
  onCancel: () => void;
}

export const PrescriptionMedicinesModal: FC<PrescriptionMedicinesModalProps> = ({
  prescriptionId,
  medicines,
  onCancel,
}) => {
  return (
    <Dialog open onClose={onCancel}>
      <Card
        sx={{
          px: 8,
          py: 6,
          width: 456,
          display: 'flex',
          flexDirection: 'column',
          gap: 6,
        }}
      >
        <Typography variant="h6">{`Recepta ${prescriptionId}`}</Typography>

        <Stack spacing={3}>
          {medicines.map((medicine) => (
            <Paper
              key={medicine.id}
              elevation={2}
              sx={{
                px: 4,
                py: 1,
                borderRadius: 1,
              }}
            >
              <Typography variant="subtitle1">
                {medicine.name} - {medicine.dosage}
              </Typography>
              <Typography variant="body2" color="primary" mt={1}>
                {medicine.amount} {medicine.amount === 1 ? 'opakowanie' : 'opakowania'}
              </Typography>
            </Paper>
          ))}
        </Stack>
      </Card>
    </Dialog>
  );
};
