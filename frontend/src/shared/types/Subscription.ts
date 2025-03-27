import { SubscriptionServiceLite } from './SubscriptionService';
import { UserLite } from './User';

export interface Subscription {
  users: UserLite[] | null;
  subscriptionServices: SubscriptionServiceLite[] | null;
}

export interface SubscriptionLite {
  id: number;
  name: string;
  price: number;
}
