import { UserRole } from './enums';

export interface User {
  id: number;
  firstName: string;
  lastName: string;
  pesel: string | null;
  passportNumber: string | null;
  email: string;
  address: string;
  password: string;
  phoneNumber: string | null;
  status: string;
  birthDate: Date | null;
  pwzNumber: string | null;
  role: UserRole;
  subscriptionId: number | null;
  specialisationIds: number[] | null;
  timeSlotIds: number[] | null;
  workTimeIds: number[] | null;
  freeDayIds: number[] | null;
  visitIds: number[] | null;
  additionalServiceIds: number[] | null;
}
