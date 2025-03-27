import { AttachmentLite } from './Attachment';
import { VisitStatus } from './enums';
import { FacilityLite } from './Facility';
import { FeedbackLite } from './Feedback';
import { NotificationLite } from './Notification';
import { PrescriptionLite } from './Prescription';
import { ReferralLite } from './Referral';
import { ServiceLite } from './Service';
import { TimeSlot } from './TimeSlot';
import { UserLite } from './User';

export interface Visit {
  facility: FacilityLite;
  service: ServiceLite;
  patient: UserLite;
  doctor: UserLite;
  feedback: FeedbackLite | null;
  attachments: AttachmentLite[] | null;
  prescriptions: PrescriptionLite[] | null;
  referrals: ReferralLite[] | null;
  notifications: NotificationLite[] | null;
}

export interface VisitLite {
  id: number;
  status: VisitStatus;
  interview: string | null;
  diagnosis: string | null;
  recommendations: string | null;
  totalCost: number | null;
  timeSlots: TimeSlot[];
  patientInformation: string | null;
}
