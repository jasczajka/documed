import { VisitLite } from './Visit';

export interface Feedback extends FeedbackLite {
  visit: VisitLite;
}

export interface FeedbackLite {
  id: number;
  rating: number;
  text: string;
}
