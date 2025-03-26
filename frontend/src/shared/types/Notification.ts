import { AdditionalServiceLite } from './AdditionalService';
import { NotificationStatus } from './enums';
import { VisitLite } from './Visit';

export interface Notification extends NotificationLite {
  visit: VisitLite | null;
  additionalService: AdditionalServiceLite | null;
}

export interface NotificationLite {
  id: number;
  status: NotificationStatus;
}
