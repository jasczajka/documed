import { AdditionalServiceLite } from './AdditionalService';
import { VisitLite } from './Visit';

export interface Attachment extends AttachmentLite {
  visit: VisitLite | null;
  additionalService: AdditionalServiceLite | null;
}
export interface AttachmentLite {
  id: number;
  url: string;
}
