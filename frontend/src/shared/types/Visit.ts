import { VisitStatus } from './enums';
import { TimeSlot } from './TimeSlot';

export interface Visit {
  id: number;
  status: VisitStatus;
  interview: string | null;
  diagnosis: string | null;
  recommendations: string | null;
  totalCost: number | null;
  facilityId: number;
  serviceId: number;
  patientInformation: string | null;
  patientId: number;
  doctorId: number;
  feedbackId: number | null;
  attachmentId: number | null;
  prescriptionIds: number[] | null;
  referralIds: number[] | null;
  notificationIds: number[] | null;
  timeSlots: TimeSlot[];
}
