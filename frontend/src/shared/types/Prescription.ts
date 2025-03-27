import { PrescribedMedicine } from './PrescribedMedicine';
import { VisitLite } from './Visit';

export interface Prescription extends PrescriptionLite {
  visit: VisitLite;
}

export interface PrescriptionLite {
  medicines: PrescribedMedicine[];
  id: number;
  accessCode: number | null;
  description: string | null;
  date: Date | null;
  expirationDate: Date;
  pesel: number | null;
  passportNumber: string | null;
}
