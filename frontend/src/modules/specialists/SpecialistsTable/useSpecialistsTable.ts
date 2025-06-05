import { useMemo } from 'react';
import { DoctorDetailsDTO, Specialization } from 'shared/api/generated/generated.schemas';
import { FilterConfig } from 'shared/components/TableFilters';
import { useSpecializationsStore } from 'shared/hooks/stores/useSpecializationsStore';
import { SpecialistFilters } from './SpecialistsTable';

const generateSpecialistsFilterConfig = (allSpecializations: Specialization[]): FilterConfig[] => [
  {
    name: 'specialistName',
    label: 'Specjalista',
    type: 'text',
  },
  {
    name: 'specialization',
    label: 'Specjalizacja',
    type: 'select',
    options: allSpecializations.map((specialization) => ({
      value: specialization.id.toString(),
      label: specialization.name,
    })),
  },
];

export const useSpecialistsTable = ({
  specialists,
  filters,
}: {
  specialists: DoctorDetailsDTO[];
  specializations: Specialization[];
  filters: SpecialistFilters;
}) => {
  const specializations = useSpecializationsStore((state) => state.specializations);
  const filterBySpecialisttName = useMemo(() => {
    if (!filters.specialistName) {
      return null;
    }
    const searchTerm = filters.specialistName.toLowerCase();
    return (specialist: DoctorDetailsDTO) =>
      `${specialist.firstName} ${specialist.lastName}`.toLowerCase().includes(searchTerm);
  }, [filters.specialistName]);

  const filterBySpecialization = useMemo(() => {
    if (!filters.specialization) return null;
    const selectedId = parseInt(filters.specialization);
    return (specialist: DoctorDetailsDTO) =>
      specialist.specializations.some((s) => s.id === selectedId);
  }, [filters.specialization]);

  const filteredSpecialists = useMemo(() => {
    const activeFilters = [filterBySpecialisttName, filterBySpecialization].filter(Boolean) as ((
      specialist: DoctorDetailsDTO,
    ) => boolean)[];

    if (activeFilters.length === 0) return specialists;

    return specialists.filter((specialist) =>
      activeFilters.every((filterFn) => filterFn(specialist)),
    );
  }, [specialists, filterBySpecialisttName, filterBySpecialization]);

  const specialistsFilterConfig = useMemo(
    () => generateSpecialistsFilterConfig(specializations),
    [],
  );

  return {
    specialistsFilterConfig,
    filteredSpecialists,
  };
};
