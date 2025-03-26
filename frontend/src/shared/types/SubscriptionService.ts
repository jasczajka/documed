import { ServiceLite } from './Service';
import { SubscriptionLite } from './Subscription';

export interface SubscriptionService extends SubscriptionServiceLite {
  subscription: SubscriptionLite;
  service: ServiceLite;
}

export interface SubscriptionServiceLite {
  discount: number;
}
