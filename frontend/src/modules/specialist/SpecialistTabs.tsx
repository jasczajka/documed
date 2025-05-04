import { BeachAccess, CalendarMonth, Person2, QueryBuilder } from '@mui/icons-material';
import { Box, Paper, Tab, Tabs } from '@mui/material';
import { FC, useState } from 'react';
import { Specialization } from 'shared/api/generated/generated.schemas';
import { EditSpecializationsTab } from './tabs/EditSpecializationsTab';

interface SpecialistTabsProps {
  doctorId: number;
  currentSpecializations: Specialization[];
  allSpecializations: Specialization[];
  handleUpdateSpecialistSpecializations: (selected: Specialization[]) => void;
  updateSpecialistSpecializationsLoading?: boolean;
}
export const SpecialistTabs: FC<SpecialistTabsProps> = ({
  currentSpecializations,
  allSpecializations,
  handleUpdateSpecialistSpecializations,
  updateSpecialistSpecializationsLoading,
}) => {
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
        aria-label="single-specialist-tabs"
      >
        <Tab icon={<BeachAccess />} iconPosition="start" label="Urlopy" />
        <Tab icon={<CalendarMonth />} iconPosition="start" label="Wizyty" />
        <Tab icon={<Person2 />} iconPosition="start" label="Specjalizacje" />
        <Tab icon={<QueryBuilder />} iconPosition="start" label="Godziny pracy" />
      </Tabs>

      <Paper sx={{ height: '100%', width: '100%', padding: 8, minHeight: '532px' }} elevation={1}>
        {index === 0 && <div>Urlopy</div>}
        {index === 1 && <div>wizyty</div>}
        {index === 2 && (
          <EditSpecializationsTab
            currentSpecializations={currentSpecializations}
            allSpecializations={allSpecializations}
            onSave={handleUpdateSpecialistSpecializations}
            loading={updateSpecialistSpecializationsLoading}
          />
        )}
        {index === 3 && <div>Godziny</div>}
      </Paper>
    </Box>
  );
};
