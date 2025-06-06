import { ReferralType } from 'shared/api/generated/generated.schemas';
import { getAllReferralTypes } from 'shared/api/generated/referral-controller/referral-controller';
import { create } from 'zustand';

interface ReferralTypeStoreType {
  referralTypeMap: Map<ReferralType, string>;
  fetchReferralTypes: () => Promise<void>;
}

export const useReferralTypesStore = create<ReferralTypeStoreType>((set) => ({
  referralTypes: [],
  referralTypeMap: new Map(),
  fetchReferralTypes: async () => {
    const fetchedReferralTypes = await getAllReferralTypes();
    const referralTypeMap = new Map<ReferralType, string>();

    fetchedReferralTypes.forEach((type) => {
      if (type.code) {
        referralTypeMap.set(type.code, type.description || type.code);
      }
    });

    set({
      referralTypeMap,
    });
  },
}));
