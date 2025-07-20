import { BeachAccess, CalendarMonth, Person2, QueryBuilder } from '@mui/icons-material';
import { Box, Paper, Tab, Tabs } from '@mui/material';
import VisitsTable from 'modules/visits/VisitsTable/VisitsTable';
import { FC } from 'react';
import {
  FreeDaysDTO,
  FreeDaysReturnDTO,
  Specialization,
  UploadWorkTimeDTO,
  VisitWithDetails,
} from 'shared/api/generated/generated.schemas';
import { EditSpecializationsTab } from './tabs/EditSpecializationsTab';
import { EditWorkTimeTab } from './tabs/EditWorkTimeTab';
import { FreeDaysTab } from './tabs/FreeDaysTab';

interface SpecialistTabsProps {
  doctorId: number;
  doctorVisits: VisitWithDetails[];
  refetchDoctorVisits: () => Promise<void>;
  refetchDoctorInfo: () => Promise<void>;
  currentSpecializations: Specialization[];
  currentWorkTimes: UploadWorkTimeDTO[];
  currentFreeDays: FreeDaysReturnDTO[];
  allSpecializations: Specialization[];
  handleUpdateSpecialistSpecializations: (selected: Specialization[]) => void;
  handleUpdateSpecialistWorkTimes: (selected: UploadWorkTimeDTO[]) => void;
  handleCancelVisit: (visitId: number) => void;
  handleNewFreeDays: (newFreeDays: FreeDaysDTO) => void;
  tabIndex: number;
  onTabChange: (index: number) => void;
  loading?: boolean;
  isArchivalVisitsOn: boolean;
  onArchivalModeToggle: () => void;
  disabled?: boolean;
}
export const SpecialistTabs: FC<SpecialistTabsProps> = ({
  doctorId,
  doctorVisits,
  refetchDoctorVisits,
  refetchDoctorInfo,
  currentSpecializations,
  currentWorkTimes,
  currentFreeDays,
  allSpecializations,
  handleUpdateSpecialistSpecializations,
  handleUpdateSpecialistWorkTimes,
  handleCancelVisit,
  handleNewFreeDays,
  tabIndex,
  onTabChange,
  loading,
  isArchivalVisitsOn,
  onArchivalModeToggle,
  disabled = false,
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
        {tabIndex === 0 && (
          <FreeDaysTab
            currentFreeDays={currentFreeDays}
            onSubmitForm={(newFreedays) => handleNewFreeDays({ userId: doctorId, ...newFreedays })}
            onSuccessfulEdit={() => refetchDoctorInfo()}
            disabled={disabled}
          />
        )}
        {tabIndex === 1 && (
          <VisitsTable
            doctorId={doctorId}
            visits={doctorVisits}
            onCancel={handleCancelVisit}
            refetchVisits={refetchDoctorVisits}
            isArchivalVisitsOn={isArchivalVisitsOn}
            onArchivalModeToggle={onArchivalModeToggle}
            displayPatientColumn
            displayDoctorColumn={false}
            disableFacilityFilter
          />
        )}
        {tabIndex === 2 && (
          <EditSpecializationsTab
            currentSpecializations={currentSpecializations}
            allSpecializations={allSpecializations}
            onSave={handleUpdateSpecialistSpecializations}
            loading={loading}
            disabled={disabled}
          />
        )}
        {tabIndex === 3 && (
          <EditWorkTimeTab
            currentWorkTimes={currentWorkTimes}
            onSave={handleUpdateSpecialistWorkTimes}
            loading={loading}
            disabled={disabled}
          />
        )}
      </Paper>
    </Box>
  );
};
