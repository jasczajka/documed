import { CalendarMonth, Cloud, CreditCard, Person2 } from '@mui/icons-material';
import { Box, Paper, Tab, Tabs } from '@mui/material';
import { FC } from 'react';

interface PatientTabsProps {
  patientId: number;

  tabIndex: number;
  onTabChange: (index: number) => void;
  // loading?: boolean;
}
export const PatientTabs: FC<PatientTabsProps> = ({
  patientId,
  tabIndex,
  onTabChange,
  // loading,
}) => {
  console.log('patient ID: ', patientId);
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
        aria-label="single-patient-tabs"
      >
        <Tab icon={<Cloud />} iconPosition="start" label="Załączniki pacjenta" />
        <Tab icon={<CalendarMonth />} iconPosition="start" label="Wizyty" />
        <Tab icon={<Person2 />} iconPosition="start" label="Dane osobowe" />
        <Tab icon={<CreditCard />} iconPosition="start" label="Abonament" />
      </Tabs>

      <Paper sx={{ height: '100%', width: '100%', padding: 8, minHeight: '532px' }} elevation={1}>
        {tabIndex === 0 && <div>Urlopy</div>}
        {tabIndex === 1 && <div>wizyty</div>}
        {tabIndex === 2 && <div>Dane osobowe</div>}
        {tabIndex === 3 && <div>Abonament</div>}
      </Paper>
    </Box>
  );
};
