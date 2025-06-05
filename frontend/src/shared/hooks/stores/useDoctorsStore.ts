import { getAllDoctors } from 'shared/api/generated/doctors-controller/doctors-controller';
import { DoctorDetailsDTO } from 'shared/api/generated/generated.schemas';
import { create } from 'zustand';

interface DoctorsStoreType {
  doctors: DoctorDetailsDTO[];
  fetchDoctors: () => Promise<void>;
}

export const useDoctorsStore = create<DoctorsStoreType>((set) => ({
  doctors: [],
  fetchDoctors: async () => {
    const fetchedDoctors = await getAllDoctors();
    set({ doctors: fetchedDoctors });
  },
}));
