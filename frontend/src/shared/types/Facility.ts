import { VisitLite } from './Visit';

export interface Facility extends FacilityLite {
  visits: VisitLite[] | null;
}

export interface FacilityLite {
  id: number;
  address: string;
  city: string;
}
