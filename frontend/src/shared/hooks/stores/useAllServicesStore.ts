import { Service } from 'shared/api/generated/generated.schemas';
import { getAllServices } from 'shared/api/generated/service-controller/service-controller';
import { create } from 'zustand';

interface AllServicesStoreType {
  allServices: Service[];
  fetchAllServices: () => Promise<void>;
}

export const useAllServicesStore = create<AllServicesStoreType>((set) => ({
  allServices: [],
  fetchAllServices: async () => {
    const fetchedAllServices = await getAllServices();
    set({ allServices: fetchedAllServices });
  },
}));
