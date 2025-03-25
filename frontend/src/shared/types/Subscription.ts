export interface Subscription {
  id: number;
  name: string;
  price: number;
  userIds: number[] | null;
  subscriptionServiceIds: number[] | null;
}
