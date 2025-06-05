import { getAllDoctors } from 'shared/api/generated/doctors-controller/doctors-controller';
import { DoctorDetailsDTO } from 'shared/api/generated/generated.schemas';
import { create } from 'zustand';

interface DoctorsStoreType {
  doctors: DoctorDetailsDTO[];
  fetchDoctors: () => Promise<void>;
}

export const useDoctorsStore = create<DoctorsStoreType>((set, get) => ({
  doctors: [],
  fetchDoctors: async () => {
    const { doctors } = get();
    if (doctors.length > 0) {
      return;
    }
    const fetchedDoctors = await getAllDoctors();
    set({ doctors: fetchedDoctors });
  },
}));
