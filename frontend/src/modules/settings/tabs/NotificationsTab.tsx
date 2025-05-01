import { FormControlLabel, Switch } from '@mui/material';
import { FC, useEffect, useState } from 'react';
import {
  areNotificationsOn,
  useToggleEmailNotifications,
} from 'shared/api/generated/user-controller/user-controller';

export const NotificationsTab: FC = () => {
  const [notificationsOn, setNotificationsOn] = useState(false);

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
    <FormControlLabel
      control={<Switch checked={notificationsOn} onChange={handleToggle} disabled={loading} />}
      label="Powiadomienia e-mail"
    />
  );
};
