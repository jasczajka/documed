import {
  CalendarMonth,
  CalendarMonthOutlined,
  Cloud,
  CreditCard,
  Person2,
} from '@mui/icons-material';
import { Box, Paper, Tab, Tabs } from '@mui/material';
import AdditionalServicesTable from 'modules/additionalServices/additionalServicesTable/AdditionalServicesTable';
import VisitsTable from 'modules/visits/VisitsTable/VisitsTable';
import { FC, useEffect } from 'react';
import {
  AdditionalServiceReturnDTO,
  FileInfoDTO,
  Service,
  VisitDTO,
} from 'shared/api/generated/generated.schemas';
import { useCancelPlannedVisit } from 'shared/api/generated/visit-controller/visit-controller';
import CancelVisitModal from 'shared/components/ConfirmationModal/CancelVisitModal';
import { PatientInfoPanelProps } from 'shared/components/PatientInfoPanel';
import { useModal } from 'shared/hooks/useModal';
import { useNotification } from 'shared/hooks/useNotification';
import { AttachmentsTab } from './tabs/AttachmentsTab';
import { PersonalDataTab } from './tabs/PersonalDataTab';

interface PatientTabsProps {
  patientInfo: PatientInfoPanelProps;
  tabIndex: number;
  onTabChange: (index: number) => void;
  patientAttachments: FileInfoDTO[];
  patientVisits: VisitDTO[];
  patientAdditionalServices: AdditionalServiceReturnDTO[];
  allServices: Service[];
  allAdditionalServices: Service[];
  refetchVisits: () => Promise<void>;
  refetchAdditionalServices: () => Promise<void>;
  refetchPatientInfo: () => Promise<void>;
}
export const PatientTabs: FC<PatientTabsProps> = ({
  patientInfo,
  tabIndex,
  onTabChange,
  patientAttachments,
  patientVisits,
  patientAdditionalServices,
  allServices,
  allAdditionalServices,
  refetchVisits,
  refetchAdditionalServices,
  refetchPatientInfo,
}) => {
  const { showNotification, NotificationComponent } = useNotification();
  const { openModal } = useModal();
  const { isPending: isCancelVisitLoading, isError: isCancelVisitError } = useCancelPlannedVisit();

  const handleCancelVisitClick = (visitId: number) => {
    openModal('cancelVisitModal', (close) => (
      <CancelVisitModal visitId={visitId} onClose={close} onSuccess={refetchVisits} />
    ));
  };

  useEffect(() => {
    if (isCancelVisitError) {
      showNotification('Nie udało się anulować wizyty', 'error');
    }
  }, [isCancelVisitError]);
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
        <Tab icon={<CalendarMonthOutlined />} iconPosition="start" label="Usługi dodatkowe" />
        <Tab icon={<Person2 />} iconPosition="start" label="Dane osobowe" />
        <Tab icon={<CreditCard />} iconPosition="start" label="Abonament" />
      </Tabs>

      <Paper sx={{ height: '100%', width: '100%', padding: 8, minHeight: '532px' }} elevation={1}>
        {tabIndex === 0 && <AttachmentsTab attachments={patientAttachments} />}
        {tabIndex === 1 && (
          <VisitsTable
            visits={patientVisits}
            allServices={allServices}
            onCancel={handleCancelVisitClick}
            loading={isCancelVisitLoading}
            patientId={patientInfo.patientId}
          />
        )}
        {tabIndex === 2 && (
          <AdditionalServicesTable
            additionalServices={patientAdditionalServices}
            allAdditionalServices={allAdditionalServices}
            loading={isCancelVisitLoading}
            refetch={refetchAdditionalServices}
            patientId={patientInfo.patientId}
          />
        )}
        {tabIndex === 3 && (
          <PersonalDataTab patientInfo={patientInfo} onSuccessfulDeactivate={refetchPatientInfo} />
        )}
        {tabIndex === 4 && <div>Abonament</div>}
      </Paper>
      <NotificationComponent />
    </Box>
  );
};
