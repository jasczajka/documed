import { Button } from '@mui/material';
import { useCallback, useState } from 'react';
import { DoctorDetailsDTO } from 'shared/api/generated/generated.schemas';
import { getAllServices } from 'shared/api/generated/service-controller/service-controller';
import { ScheduleVisitModal } from 'shared/components/ScheduleVisitModal';
import { useModal } from 'shared/hooks/useModal';

export const sampleDoctors: DoctorDetailsDTO[] = [
  {
    id: 1,
    firstName: 'Anna',
    lastName: 'Kowalska',
    email: 'anna.kowalska@example.com',
    specializations: [],
  },
  {
    id: 2,
    firstName: 'Piotr',
    lastName: 'Nowak',
    email: 'piotr.nowak@example.com',
    specializations: [],
  },
  {
    id: 3,
    firstName: 'Maria',
    lastName: 'Wiśniewska',
    email: 'maria.wisniewska@example.com',
    specializations: [{ id: 1, name: 'dupa' }],
  },
  {
    id: 4,
    firstName: 'Tomasz',
    lastName: 'Zieliński',
    email: 'tomasz.zielinski@example.com',
    specializations: [],
  },
];
const VisitDatepickerTestPage = () => {
  const [isInitialDataLoading, setIsInitialDataLoading] = useState(false);

  const { openModal, closeModal } = useModal();
  const handleScheduleVisitClick = useCallback(async () => {
    setIsInitialDataLoading(true);

    const services = await getAllServices();

    setIsInitialDataLoading(false);

    openModal(
      'scheduleVisitModal',
      <ScheduleVisitModal
        allDoctors={sampleDoctors}
        allServices={services}
        patientId={1}
        patientFullName="Jan Czajka"
        patientAge={25}
        onConfirm={(data) => console.log(data)}
        onCancel={() => closeModal('scheduleVisitModal')}
        loading={isInitialDataLoading}
      />,
    );
  }, [openModal, closeModal]);
  return (
    <main className="flex h-full w-dvw flex-col items-center justify-center">
      <Button onClick={() => handleScheduleVisitClick()}>Umów wizytę</Button>
    </main>
  );
};

export default VisitDatepickerTestPage;
