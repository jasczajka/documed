import { NotificationStatus } from './enums';

export interface Notification {
  id: number;
  status: NotificationStatus;
  visitId: number | null;
  additionalServiceId: number | null;
}
