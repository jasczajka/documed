import { NotificationsActive, Person } from '@mui/icons-material';
import { Box, Paper, Tab, Tabs } from '@mui/material';
import { FC, useState } from 'react';
import { AccountTab } from './tabs/AccountTab';
import { NotificationsTab } from './tabs/NotificationsTab';

export const SettingsTabs: FC = () => {
  const [index, setIndex] = useState(0);

  return (
    <Box
      sx={{
        display: 'flex',
        flexDirection: 'column',
        height: '100%',
        flex: 1,
      }}
    >
      <Tabs
        sx={{
          '& .MuiTab-root': {
            paddingBottom: '0',
            '&.Mui-selected svg': {
              color: 'primary.main',
            },
            '& svg': {
              color: 'text.secondary',
            },
          },
        }}
        value={index}
        onChange={(_, value) => setIndex(value)}
        aria-label="administration-tabs"
      >
        <Tab icon={<Person />} iconPosition="start" label="Konto" />
        <Tab icon={<NotificationsActive />} iconPosition="start" label="Powiadomienia" />
      </Tabs>

      <Paper sx={{ height: '100%', width: '100%', padding: 8, minHeight: '532px' }} elevation={1}>
        {index === 0 && <AccountTab />}
        {index === 1 && <NotificationsTab />}
      </Paper>
    </Box>
  );
};
