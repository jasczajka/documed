import { Button, Dialog, DialogActions, DialogTitle, TextField } from '@mui/material';
import { FC, useState } from 'react';
import { ServiceWithDiscount } from '../SubscriptionServicesTable';

interface DiscountEditModalProps {
  service: ServiceWithDiscount;
  subscriptionId: number;
  onSave: (subscriptionId: number, serviceId: number, discount: number) => Promise<void>;
  onCancel: () => void;
}

export const DiscountEditModal: FC<DiscountEditModalProps> = ({
  service,
  subscriptionId,
  onSave,
  onCancel,
}) => {
  const [discount, setDiscount] = useState(service.discount);
  const [isSaving, setIsSaving] = useState(false);

  const handleSave = async () => {
    setIsSaving(true);
    try {
      await onSave(subscriptionId, service.id, discount);
    } finally {
      setIsSaving(false);
    }
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
            minWidth: 360,
          },
        },
      }}
    >
      <DialogTitle> Edytuj zniżkę dla {service.name}</DialogTitle>
      <TextField
        label="Zniżka (%)"
        type="text"
        fullWidth
        value={discount.toString()}
        onChange={(e) => {
          const val = e.target.value;

          if (!/^\d{0,3}$/.test(val)) return;

          const num = Number(val);

          if (num > 100) {
            setDiscount(100);
          } else {
            setDiscount(num);
          }
        }}
        inputProps={{
          inputMode: 'numeric',
          pattern: '[0-9]*',
          maxLength: 3,
        }}
        sx={{ mb: 3 }}
      />
      <DialogActions
        sx={{
          mt: 2,
          width: '100%',
          display: 'flex',
          gap: 3,
        }}
      >
        <Button
          onClick={onCancel}
          color="error"
          variant="outlined"
          disabled={isSaving}
          sx={{
            flex: 1,
            minWidth: 100,
          }}
        >
          Anuluj
        </Button>
        <Button
          onClick={handleSave}
          variant="contained"
          disabled={isSaving}
          loading={isSaving}
          sx={{
            flex: 1,
            minWidth: 100,
          }}
        >
          Zapisz
        </Button>
      </DialogActions>
    </Dialog>
  );
};
