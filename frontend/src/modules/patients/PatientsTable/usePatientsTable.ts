import { useMemo } from 'react';
import { PatientDetailsDTO } from 'shared/api/generated/generated.schemas';
import { FilterConfig } from 'shared/components/TableFilters';
import { PatientsFilters } from './PatientsTable';

const generatePatientsFilterConfig = (): FilterConfig[] => [
  {
    name: 'patientName',
    label: 'Pacjent',
    type: 'text',
  },
  {
    name: 'pesel',
    label: 'Pesel',
    type: 'text',
  },
];

export const usePatientsTable = ({
  patients,
  filters,
}: {
  patients: PatientDetailsDTO[];
  filters: PatientsFilters;
}) => {
  const filterByPatientName = useMemo(() => {
    if (!filters.patientName) {
      return null;
    }
    const searchTerm = filters.patientName.toLowerCase();
    return (patient: PatientDetailsDTO) =>
      `${patient.firstName} ${patient.lastName}`.toLowerCase().includes(searchTerm);
  }, [filters.patientName]);

  const filterByPesel = useMemo(() => {
    if (!filters.pesel) {
      return null;
    }
    const searchTerm = filters.pesel.toLowerCase();
    return (patient: PatientDetailsDTO) => (patient.pesel ?? '').toLowerCase().includes(searchTerm);
  }, [filters.pesel]);

  const filteredPatients = useMemo(() => {
    const activeFilters = [filterByPatientName, filterByPesel].filter(Boolean) as ((
      patient: PatientDetailsDTO,
    ) => boolean)[];

    if (activeFilters.length === 0) return patients;

    return patients.filter((patient) => activeFilters.every((filterFn) => filterFn(patient)));
  }, [patients, filterByPatientName, filterByPesel]);

  const patientsFilterConfig = useMemo(() => generatePatientsFilterConfig(), []);

  return {
    patientsFilterConfig,
    filteredPatients,
  };
};
