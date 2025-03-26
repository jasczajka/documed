import { AdditionalServiceLite } from './AdditionalService';
import { ServiceType } from './enums';
import { SpecializationLite } from './Specialization';
import { VisitLite } from './Visit';

export interface Service extends ServiceLite {
  specializations: SpecializationLite[] | null;
  visits: VisitLite[] | null;
  additionalServices: AdditionalServiceLite[] | null;
}

export interface ServiceLite {
  id: number;
  name: string;
  price: number;
  type: ServiceType;
  estimatedTime: number | null;
}
