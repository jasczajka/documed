import { BeachAccess, CalendarMonth, Person2, QueryBuilder } from '@mui/icons-material';
import { Box, Paper, Tab, Tabs } from '@mui/material';
import { FC } from 'react';
import { Specialization } from 'shared/api/generated/generated.schemas';
import { WorkTimeWithoutIdAndUser } from 'src/pages/SingleSpecialistPage';
import { EditSpecializationsTab } from './tabs/EditSpecializationsTab';
import { EditWorkTimeTab } from './tabs/EditWorkTimeTab';

interface SpecialistTabsProps {
  doctorId: number;
  currentSpecializations: Specialization[];
  currentWorkTimes: WorkTimeWithoutIdAndUser[];
  allSpecializations: Specialization[];
  handleUpdateSpecialistSpecializations: (selected: Specialization[]) => void;
  handleUpdateSpecialistWorkTimes: (selected: WorkTimeWithoutIdAndUser[]) => void;
  tabIndex: number;
  onTabChange: (index: number) => void;
  loading?: boolean;
}
export const SpecialistTabs: FC<SpecialistTabsProps> = ({
  currentSpecializations,
  currentWorkTimes,
  allSpecializations,
  handleUpdateSpecialistSpecializations,
  handleUpdateSpecialistWorkTimes,
  tabIndex,
  onTabChange,
  loading,
}) => {
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
        value={tabIndex}
        onChange={(_, value) => onTabChange(value)}
        aria-label="single-specialist-tabs"
      >
        <Tab icon={<BeachAccess />} iconPosition="start" label="Urlopy" />
        <Tab icon={<CalendarMonth />} iconPosition="start" label="Wizyty" />
        <Tab icon={<Person2 />} iconPosition="start" label="Specjalizacje" />
        <Tab icon={<QueryBuilder />} iconPosition="start" label="Godziny pracy" />
      </Tabs>

      <Paper sx={{ height: '100%', width: '100%', padding: 8, minHeight: '532px' }} elevation={1}>
        {tabIndex === 0 && <div>Urlopy</div>}
        {tabIndex === 1 && <div>wizyty</div>}
        {tabIndex === 2 && (
          <EditSpecializationsTab
            currentSpecializations={currentSpecializations}
            allSpecializations={allSpecializations}
            onSave={handleUpdateSpecialistSpecializations}
            loading={loading}
          />
        )}
        {tabIndex === 3 && (
          <EditWorkTimeTab
            currentWorkTimes={currentWorkTimes}
            onSave={handleUpdateSpecialistWorkTimes}
            loading={loading}
          />
        )}
      </Paper>
    </Box>
  );
};
