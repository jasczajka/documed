import { Specialization } from 'shared/api/generated/generated.schemas';
import { getAllSpecializations } from 'shared/api/generated/specialization-controller/specialization-controller';
import { create } from 'zustand';

interface SpecializationsStoreType {
  specializations: Specialization[];
  fetchSpecializations: () => Promise<void>;
}

export const useSpecializationsStore = create<SpecializationsStoreType>((set) => ({
  specializations: [],
  fetchSpecializations: async () => {
    const fetchedSpecializations = await getAllSpecializations();
    set({ specializations: fetchedSpecializations });
  },
}));
