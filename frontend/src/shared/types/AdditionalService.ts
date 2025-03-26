import { AttachmentLite } from './Attachment';
import { VisitStatus } from './enums';
import { NotificationLite } from './Notification';
import { ServiceLite } from './Service';
import { UserLite } from './User';

export interface AdditionalService extends AdditionalServiceLite {
  fulfiller: UserLite;
  service: ServiceLite;
  notifications: NotificationLite[] | null;
  attachments: AttachmentLite[] | null;
}

export interface AdditionalServiceLite {
  id: number;
  description: string | null;
  date: Date;
  status: VisitStatus;
}
