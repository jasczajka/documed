import {
  ArrowForwardIosOutlined,
  CalendarMonth,
  CalendarMonthOutlined,
  Cloud,
  CreditCard,
  MedicationOutlined,
  Person2,
} from '@mui/icons-material';
import { Box, Paper, Tab, Tabs } from '@mui/material';
import AdditionalServicesTable from 'modules/additionalServices/additionalServicesTable/AdditionalServicesTable';
import { PrescriptionsTable } from 'modules/prescriptions/components/PrescriptionsTable/PrescriptionsTable';
import { ReferralsTable } from 'modules/referrals/components/ReferralsTable';
import VisitsTable from 'modules/visits/VisitsTable/VisitsTable';
import { FC, useEffect } from 'react';
import {
  AdditionalServiceWithDetails,
  FileInfoDTO,
  PatientDetailsDTO,
  Prescription,
  ReturnReferralDTO,
  Service,
  VisitWithDetails,
} from 'shared/api/generated/generated.schemas';
import { useCancelPlannedVisit } from 'shared/api/generated/visit-controller/visit-controller';
import CancelVisitModal from 'shared/components/ConfirmationModal/CancelVisitModal';
import { useAuth } from 'shared/hooks/useAuth';
import { useModal } from 'shared/hooks/useModal';
import { useNotification } from 'shared/hooks/useNotification';
import { AttachmentsTab } from './tabs/AttachmentsTab';
import { PersonalDataTab } from './tabs/PersonalDataTab';
import { SubscriptionTab } from './tabs/SubscriptionTab';

interface PatientTabsProps {
  patientDetails: PatientDetailsDTO;
  patientSubscriptionId: number | null;
  tabIndex: number;
  onTabChange: (index: number) => void;
  patientAttachments: FileInfoDTO[];
  patientVisits: VisitWithDetails[];
  patientAdditionalServices: AdditionalServiceWithDetails[];
  patientPrescriptions: Prescription[];
  patientReferrals: ReturnReferralDTO[];
  allServices: Service[];
  allAdditionalServices: Service[];
  refetchVisits: () => Promise<void>;
  refetchAdditionalServices: () => Promise<void>;
  refetchPatientInfo: () => Promise<void>;
  isArchivalModeOn: boolean;
  onArchivalModeToggle: () => void;
}

export const PatientTabs: FC<PatientTabsProps> = ({
  patientDetails,
  patientSubscriptionId,
  tabIndex,
  onTabChange,
  patientAttachments,
  patientVisits,
  patientAdditionalServices,
  patientPrescriptions,
  patientReferrals,
  allServices,
  allAdditionalServices,
  refetchVisits,
  refetchAdditionalServices,
  refetchPatientInfo,
  isArchivalModeOn,
  onArchivalModeToggle,
}) => {
  const { showNotification, NotificationComponent } = useNotification();
  const { openModal } = useModal();
  const { isWardClerk } = useAuth();

  const { isPending: isCancelVisitLoading, isError: isCancelVisitError } = useCancelPlannedVisit();

  const handleCancelVisitClick = (visitId: number) => {
    openModal('cancelVisitModal', (close) => (
      <CancelVisitModal
        visitId={visitId}
        onClose={close}
        onSuccess={() => {
          refetchVisits();
          showNotification('Pomyślnie odwołano wizytę!', 'success');
        }}
      />
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
        <Tab disabled={!isWardClerk} icon={<CreditCard />} iconPosition="start" label="Abonament" />
        <Tab icon={<MedicationOutlined />} iconPosition="start" label="Recepty" />
        <Tab
          disabled={isWardClerk}
          icon={<ArrowForwardIosOutlined />}
          iconPosition="start"
          label="Skierowania"
        />
      </Tabs>

      <Paper sx={{ height: '100%', width: '100%', padding: 8, minHeight: '532px' }} elevation={1}>
        {tabIndex === 0 && <AttachmentsTab attachments={patientAttachments} />}
        {tabIndex === 1 && (
          <VisitsTable
            visits={patientVisits}
            allServices={allServices}
            onCancel={handleCancelVisitClick}
            loading={isCancelVisitLoading}
            refetchVisits={async () => {
              await refetchVisits();
            }}
            isArchivalVisitsOn={isArchivalModeOn}
            onArchivalModeToggle={onArchivalModeToggle}
          />
        )}
        {tabIndex === 2 && (
          <AdditionalServicesTable
            additionalServices={patientAdditionalServices}
            allAdditionalServices={allAdditionalServices}
            loading={isCancelVisitLoading}
            refetch={refetchAdditionalServices}
            isArchivalAdditionalServicesOn={isArchivalModeOn}
            onArchivalModeToggle={onArchivalModeToggle}
          />
        )}
        {tabIndex === 3 && (
          <PersonalDataTab patientDetails={patientDetails} onSuccessfulEdit={refetchPatientInfo} />
        )}
        {tabIndex === 4 && (
          <SubscriptionTab
            patientSubscriptionId={patientSubscriptionId}
            allServices={allServices}
            refetch={refetchPatientInfo}
          />
        )}
        {tabIndex === 5 && <PrescriptionsTable prescriptions={patientPrescriptions} />}
        {tabIndex === 6 && <ReferralsTable referrals={patientReferrals} />}
      </Paper>
      <NotificationComponent />
    </Box>
  );
};
