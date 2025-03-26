import { ReferralType } from './enums';
import { VisitLite } from './Visit';

export interface Referall extends ReferralLite {
  visit: VisitLite;
}

export interface ReferralLite {
  id: number;
  diagnosis: string | null;
  type: ReferralType;
  expirationDate: Date | null;
}
