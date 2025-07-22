import { FormControlLabel, Switch, Typography } from '@mui/material';
import { FC, useEffect, useState } from 'react';
import {
  areNotificationsOn,
  useToggleEmailNotifications,
} from 'shared/api/generated/user-controller/user-controller';
import { useAuth } from 'shared/hooks/useAuth';

export const NotificationsTab: FC = () => {
  const [notificationsOn, setNotificationsOn] = useState(false);
  const { isPatient } = useAuth();

  const { mutateAsync: toggleEmailNotifications, isPending: loading } =
    useToggleEmailNotifications();

  const handleToggle = async () => {
    await toggleEmailNotifications();
    setNotificationsOn((prev) => !prev);
  };

  useEffect(() => {
    const checkNotifications = async () => {
      const status = await areNotificationsOn();
      setNotificationsOn(status);
    };
    checkNotifications();
  }, []);

  return (
    <>
      {isPatient ? (
        <FormControlLabel
          control={<Switch checked={notificationsOn} onChange={handleToggle} disabled={loading} />}
          label="Powiadomienia e-mail"
        />
      ) : (
        <Typography>Powiadomienia e-mail są dostępne tylko dla pacjentów</Typography>
      )}
    </>
  );
};
