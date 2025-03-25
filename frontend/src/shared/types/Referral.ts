import { ReferralType } from './enums';

export interface Referral {
  id: number;
  diagnosis: string | null;
  type: ReferralType;
  visitId: number;
  expirationDate: Date | null;
}
