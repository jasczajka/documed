import { CalendarMonth, Cloud, CreditCard, Person2 } from '@mui/icons-material';
import { Box, Paper, Tab, Tabs } from '@mui/material';
import VisitsTable from 'modules/visits/VisitsTable/VisitsTable';
import { FC } from 'react';
import { FileInfoDTO, Service, VisitDTO } from 'shared/api/generated/generated.schemas';
import { PatientInfoPanelProps } from 'shared/components/PatientInfoPanel';
import { AttachmentsTab } from './tabs/AttachmentsTab';
import { PersonalDataTab } from './tabs/PersonalDataTab';

interface PatientTabsProps {
  patientInfo: PatientInfoPanelProps;
  tabIndex: number;
  onTabChange: (index: number) => void;
  patientAttachments: FileInfoDTO[];
  patientVisits: VisitDTO[];
  allServices: Service[];
  refetch: () => void;
  // loading?: boolean;
}
export const PatientTabs: FC<PatientTabsProps> = ({
  patientInfo,
  tabIndex,
  onTabChange,
  patientAttachments,
  patientVisits,
  allServices,
  refetch,
  // loading,
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
        aria-label="single-patient-tabs"
      >
        <Tab icon={<Cloud />} iconPosition="start" label="Załączniki pacjenta" />
        <Tab icon={<CalendarMonth />} iconPosition="start" label="Wizyty" />
        <Tab icon={<Person2 />} iconPosition="start" label="Dane osobowe" />
        <Tab icon={<CreditCard />} iconPosition="start" label="Abonament" />
      </Tabs>

      <Paper sx={{ height: '100%', width: '100%', padding: 8, minHeight: '532px' }} elevation={1}>
        {tabIndex === 0 && <AttachmentsTab attachments={patientAttachments} />}
        {tabIndex === 1 && <VisitsTable visits={patientVisits} allServices={allServices} />}
        {tabIndex === 2 && (
          <PersonalDataTab patientInfo={patientInfo} onSuccessfulDeactivate={refetch} />
        )}
        {tabIndex === 3 && <div>Abonament</div>}
      </Paper>
    </Box>
  );
};
