import { getAllFacilities } from 'shared/api/generated/facility-controller/facility-controller';
import { FacilityInfoReturnDTO } from 'shared/api/generated/generated.schemas';
import { create } from 'zustand';

interface FacilityStoreType {
  facilities: FacilityInfoReturnDTO[];
  fetchFacilities: () => Promise<void>;
}

export const useFacilityStore = create<FacilityStoreType>((set) => ({
  facilities: [],
  fetchFacilities: async () => {
    const fetchedFacilities = await getAllFacilities();
    set({ facilities: fetchedFacilities });
  },
}));
