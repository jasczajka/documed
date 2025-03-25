import { ServiceType } from './enums';

export interface Service {
  id: number;
  name: string;
  price: number;
  type: ServiceType;
  estimatedTime: number | null;
  specialisationIds: number[] | null;
  visitIds: number[] | null;
  additionalServiceIds: number[] | null;
}
