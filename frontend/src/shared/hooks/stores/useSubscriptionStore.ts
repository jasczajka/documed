import { Subscription } from 'shared/api/generated/generated.schemas';
import { getAllSubscriptions } from 'shared/api/generated/subscription-controller/subscription-controller';
import { create } from 'zustand';

interface SubscriptionStoreType {
  subscriptions: Subscription[];
  fetchSubscriptions: () => Promise<void>;
}

export const useSubscriptionStore = create<SubscriptionStoreType>((set, get) => ({
  subscriptions: [],
  fetchSubscriptions: async () => {
    const { subscriptions } = get();
    if (subscriptions.length > 0) {
      return;
    }
    const fetchedSubscriptions = await getAllSubscriptions();
    set({ subscriptions: fetchedSubscriptions });
  },
}));
