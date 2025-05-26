import {
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  DialogContentText,
  DialogTitle,
} from '@mui/material';
import { FC } from 'react';

export interface ConfirmationModalProps {
  title?: string;
  message?: string;
  confirmText?: string;
  cancelText?: string;
  onConfirm: () => void;
  onCancel: () => void;
  loading?: boolean;
}

export const ConfirmationModal: FC<ConfirmationModalProps> = ({
  title = 'Jesteś pewien?',
  message = 'Ta akcja jest nieodwracalna!',
  confirmText = 'Potwierdź',
  cancelText = 'Anuluj',
  onConfirm,
  onCancel,
  loading,
}) => {
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
      <DialogTitle>{title}</DialogTitle>
      <DialogContent>
        <DialogContentText sx={{ mt: 1.5 }}>{message}</DialogContentText>
      </DialogContent>
      <DialogActions sx={{ mt: 2, justifyContent: 'flex-end', gap: 1 }}>
        <Button onClick={onCancel} color="error" variant="outlined" sx={{ minWidth: 100 }}>
          {cancelText}
        </Button>
        <Button
          onClick={onConfirm}
          color="success"
          variant="contained"
          sx={{ minWidth: 100 }}
          loading={loading}
          disabled={loading}
        >
          {confirmText}
        </Button>
      </DialogActions>
    </Dialog>
  );
};

export default ConfirmationModal;
