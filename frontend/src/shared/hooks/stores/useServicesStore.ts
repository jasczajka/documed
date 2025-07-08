import { Service, ServiceType } from 'shared/api/generated/generated.schemas';
import { getAllServices } from 'shared/api/generated/service-controller/service-controller';
import { create } from 'zustand';

interface AllServicesStoreType {
  regularServices: Service[];
  addditionalServices: Service[];
  fetchAllServices: () => Promise<void>;
}

export const useServicesStore = create<AllServicesStoreType>((set) => ({
  regularServices: [],
  addditionalServices: [],
  fetchAllServices: async () => {
    const fetchedAllServices = await getAllServices();
    const regularServices: Service[] = [];
    const addditionalServices: Service[] = [];

    for (const service of fetchedAllServices) {
      if (service.type === ServiceType.REGULAR_SERVICE) {
        regularServices.push(service);
      } else if (service.type === ServiceType.ADDITIONAL_SERVICE) {
        addditionalServices.push(service);
      }
    }

    set({ regularServices, addditionalServices });
  },
}));
