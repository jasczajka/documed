import { Alert, Snackbar } from '@mui/material';
import { useState } from 'react';
import { appConfig } from 'shared/appConfig';

type NotificationSeverity = 'success' | 'error' | 'info' | 'warning';

export const useNotification = () => {
  const [open, setOpen] = useState(false);
  const [message, setMessage] = useState('');
  const [severity, setSeverity] = useState<NotificationSeverity>('info');

  const showNotification = (newMessage: string, newSeverity: NotificationSeverity = 'info') => {
    setMessage(newMessage);
    setSeverity(newSeverity);
    setOpen(true);
  };

  const handleClose = () => {
    setOpen(false);
  };

  const NotificationComponent = () => (
    <Snackbar
      open={open}
      onClose={handleClose}
      autoHideDuration={appConfig.snackBarDuration}
      anchorOrigin={{ vertical: 'top', horizontal: 'center' }}
    >
      <Alert onClose={handleClose} severity={severity} sx={{ width: '100%' }}>
        {message}
      </Alert>
    </Snackbar>
  );

  return { showNotification, NotificationComponent };
};
