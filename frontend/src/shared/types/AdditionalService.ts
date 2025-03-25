import { VisitStatus } from './enums';

export interface AdditionalService {
  id: number;
  description: string | null;
  date: Date;
  fulfillerId: number;
  serviceId: number;
  status: VisitStatus;
  notificationIds: number[] | null;
  attachmentIds: number[] | null;
}
