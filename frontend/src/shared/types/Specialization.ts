import { ServiceLite } from './Service';
import { UserLite } from './User';

export interface Specialization extends SpecializationLite {
  services: ServiceLite[] | null;
  users: UserLite[] | null;
}

export interface SpecializationLite {
  id: number;
  name: string;
}
