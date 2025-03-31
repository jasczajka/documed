import {
  Avatar,
  Box,
  Button,
  Card,
  Dialog,
  DialogActions,
  DialogContent,
  Rating,
  Stack,
  TextField,
  Typography,
} from '@mui/material';
import { FC, useCallback, useState } from 'react';

export interface ReviewModalProps {
  title?: string;
  visitId: string;
  specialistFullName: string;
  confirmText?: string;
  cancelText?: string;
  onConfirm: (rating: number, visitId: string, additionalInfo?: string) => void;
  onCancel: () => void;
}

export const ReviewModal: FC<ReviewModalProps> = ({
  title = 'Oceń wizytę lub Usługę',
  visitId,
  specialistFullName,
  confirmText = 'Potwierdź',
  cancelText = 'Anuluj',
  onConfirm,
  onCancel,
}) => {
  const [rating, setRating] = useState<number | null>(null);
  const [additionalInfo, setAdditionalInfo] = useState('');

  const handleConfirm = useCallback(() => {
    if (rating === null) {
      return;
    }
    onConfirm(rating, visitId, additionalInfo);
  }, [rating]);

  return (
    <Dialog open onClose={onCancel}>
      <Card
        sx={{
          px: 8,
          py: 6,
          width: 414,
          display: 'flex',
          flexDirection: 'column',
          gap: 6,
        }}
      >
        <Typography variant="h6" fontWeight="bold">
          {title}
        </Typography>

        <DialogContent sx={{ p: 0 }}>
          <Stack spacing={3}>
            <Box sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
              <Avatar sx={{ width: 24, height: 24 }} />
              <Typography variant="body1">{specialistFullName}</Typography>
            </Box>
            <Rating value={rating} onChange={(_, newValue) => setRating(newValue)} />
            <TextField
              fullWidth
              multiline
              rows={3}
              label="Dodatkowe uwagi"
              value={additionalInfo}
              onChange={(e) => setAdditionalInfo(e.target.value)}
            />
          </Stack>
        </DialogContent>

        <DialogActions sx={{ padding: 0 }}>
          <Stack direction="row" spacing={4} width="100%">
            <Button fullWidth onClick={onCancel} color="error" variant="contained">
              {cancelText}
            </Button>
            <Button
              fullWidth
              onClick={handleConfirm}
              color="success"
              variant="contained"
              disabled={rating === null}
            >
              {confirmText}
            </Button>
          </Stack>
        </DialogActions>
      </Card>
    </Dialog>
  );
};
