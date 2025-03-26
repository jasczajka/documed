import { AdditionalServiceLite } from './AdditionalService';
import { UserRole } from './enums';
import { SpecializationLite } from './Specialization';
import { SubscriptionLite } from './Subscription';
import { TimeSlot } from './TimeSlot';
import { VisitLite } from './Visit';
import { WorkTimeLite } from './WorkTime';

export interface User extends UserLite {
  subscription: SubscriptionLite | null;
  specializations: SpecializationLite[] | null;
  timeSlots: TimeSlot[] | null;
  workTimes: WorkTimeLite[] | null;
  freeDays: WorkTimeLite[] | null;
  visits: VisitLite[] | null;
  additionalServices: AdditionalServiceLite[] | null;
}

export interface UserLite {
  id: number;
  firstName: string;
  lastName: string;
  email: string;
  role: UserRole;
  pwzNumber: string | null;
  pesel: string | null;
  passportNumber: string | null;
  address: string;
  password: string;
  phoneNumber: string | null;
  status: string;
  birthDate: Date | null;
}
