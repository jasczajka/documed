import { useMemo } from 'react';
import { Service, VisitDTO, VisitStatus } from 'shared/api/generated/generated.schemas';
import { FilterConfig } from 'shared/components/TableFilters';
import { VisitsFilters } from './VisitsTable';

const generateVisitsFilterConfig = (allServices: Service[]): FilterConfig[] => [
  {
    name: 'status',
    label: 'Status wizyty',
    type: 'select',
    options: [
      { value: VisitStatus.CANCELLED.toString(), label: 'Anulowana' },
      { value: VisitStatus.CLOSED.toString(), label: 'Zakończona' },
      { value: VisitStatus.IN_PROGRESS.toString(), label: 'W trakcie' },
      { value: VisitStatus.PLANNED.toString(), label: 'Zaplanowana' },
    ],
  },
  {
    name: 'patientName',
    label: 'Pacjent',
    type: 'text',
  },

  {
    name: 'service',
    label: 'Usługa',
    type: 'select',
    options: allServices.map((service) => ({
      value: service.name,
      label: service.name,
    })),
  },
  {
    name: 'specialist',
    label: 'Specjalista',
    type: 'text',
  },
  {
    name: 'dateFrom',
    label: 'Od',
    type: 'datetime',
  },
  {
    name: 'dateTo',
    label: 'Do',
    type: 'datetime',
  },
];

export const useVisitsTable = ({
  visits,
  filters,
  allServices,
}: {
  visits: VisitDTO[];
  filters: VisitsFilters;
  allServices: Service[];
}) => {
  const filterByStatus = useMemo(() => {
    if (!filters.status) {
      return null;
    }
    return (visit: VisitDTO) => visit.status.toString() === filters.status;
  }, [filters.status]);

  const filterByPatientName = useMemo(() => {
    if (!filters.patientName) {
      return null;
    }
    const searchTerm = filters.patientName.toLowerCase();
    return (visit: VisitDTO) => visit.patientFullName.toLowerCase().includes(searchTerm);
  }, [filters.patientName]);

  const filterByService = useMemo(() => {
    if (!filters.service) return null;
    const searchTerm = filters.service.toLowerCase();
    return (visit: VisitDTO) => visit.serviceName.toLowerCase().includes(searchTerm);
  }, [filters.service]);

  const filterBySpecialist = useMemo(() => {
    if (!filters.specialist) {
      return null;
    }
    const searchTerm = filters.specialist.toLowerCase();
    return (visit: VisitDTO) => {
      return visit.doctorFullName.toLowerCase().includes(searchTerm);
    };
  }, [filters.specialist]);

  const filterByDateRange = useMemo(() => {
    if (!filters.dateFrom && !filters.dateTo) {
      return null;
    }

    return (visit: VisitDTO) => {
      if (!visit.date || !visit.startTime) {
        return false;
      }

      const visitDateString = visit.date;
      const visitStartTimeString = visit.startTime;
      const visitDateTime = new Date(`${visitDateString}T${visitStartTimeString}`);

      const fromDate = filters.dateFrom ? new Date(filters.dateFrom) : null;
      const toDate = filters.dateTo ? new Date(filters.dateTo) : null;

      return (!fromDate || visitDateTime >= fromDate) && (!toDate || visitDateTime <= toDate);
    };
  }, [filters.dateFrom, filters.dateTo]);

  const filteredVisits = useMemo(() => {
    const activeFilters = [
      filterByStatus,
      filterByPatientName,
      filterByService,
      filterBySpecialist,
      filterByDateRange,
    ].filter(Boolean) as ((visit: VisitDTO) => boolean)[];

    if (activeFilters.length === 0) return visits;

    return visits.filter((visit) => activeFilters.every((filterFn) => filterFn(visit)));
  }, [
    visits,
    filterByStatus,
    filterByPatientName,
    filterByService,
    filterBySpecialist,
    filterByDateRange,
  ]);

  const visitsFilterConfig = useMemo(() => generateVisitsFilterConfig(allServices), [allServices]);

  return {
    visitsFilterConfig,
    filteredVisits,
  };
};
