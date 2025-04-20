import { Add, MiscellaneousServices, PersonAdd } from '@mui/icons-material';
import { Box, Paper, Tab, Tabs } from '@mui/material';
import { AddServiceTab } from 'modules/admin/tabs/AddServiceTab';
import { EditServiceTab } from 'modules/admin/tabs/EditServiceTab';
import { FC, useState } from 'react';

export const AdminTabs: FC = () => {
  const [index, setIndex] = useState(0);

  return (
    <Box className="h-full pt-10">
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
        <Tab icon={<Add />} iconPosition="start" label="Dodaj usługę" />
        <Tab icon={<MiscellaneousServices />} iconPosition="start" label="Edytuj usługę" />
        <Tab icon={<PersonAdd />} iconPosition="start" label="Dodaj pracownika" />
      </Tabs>

      <Paper elevation={1} className="h-full min-h-[532px] w-full p-8">
        {index === 0 && <AddServiceTab />}
        {index === 1 && <EditServiceTab />}
        {index === 2 && <div>Dodaj pracownika content here</div>}
      </Paper>
    </Box>
  );
};
