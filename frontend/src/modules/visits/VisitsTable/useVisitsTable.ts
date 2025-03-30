import { format } from 'date-fns';
import { useMemo } from 'react';
import { appConfig } from 'shared/appConfig';
import { FilterConfig } from 'shared/components/TableFilters';
import { ServiceType } from 'shared/types/enums';
import { VisitLite } from 'shared/types/Visit';
import { VisitsFilters } from './VisitsTable';

const visitsFilterConfig: FilterConfig[] = [
  {
    name: 'serviceType',
    label: 'Rodzaj usługi',
    type: 'select',
    options: [
      { value: ServiceType.REGULAR_SERVICE.toString(), label: 'Wizyta' },
      { value: ServiceType.ADDITIONAL_SERVICE.toString(), label: 'Dodatkowa usługa' },
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
    options: [
      { value: 'Kardiologia', label: 'Kardiologia' },
      { value: 'Stomatologia', label: 'Stomatologia' },
    ],
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
}: {
  visits: VisitLite[];
  filters: VisitsFilters;
}) => {
  const filterByServiceType = useMemo(() => {
    if (!filters.serviceType) return null;
    return (visit: VisitLite) => visit.service.type === filters.serviceType;
  }, [filters.serviceType]);

  const filterByPatientName = useMemo(() => {
    if (!filters.patientName) return null;
    const searchTerm = filters.patientName.toLowerCase();
    return (visit: VisitLite) =>
      `${visit.patient.firstName} ${visit.patient.lastName}`.toLowerCase().includes(searchTerm);
  }, [filters.patientName]);

  const filterByService = useMemo(() => {
    if (!filters.service) return null;
    const searchTerm = filters.service.toLowerCase();
    return (visit: VisitLite) => visit.service.name.toLowerCase().includes(searchTerm);
  }, [filters.service]);

  const filterBySpecialist = useMemo(() => {
    if (!filters.specialist) return null;
    const searchTerm = filters.specialist.toLowerCase();
    return (visit: VisitLite) => {
      if (!visit.doctor) return false;
      return `${visit.doctor.firstName} ${visit.doctor.lastName}`
        .toLowerCase()
        .includes(searchTerm);
    };
  }, [filters.specialist]);

  const filterByDateRange = useMemo(() => {
    if (!filters.dateFrom && !filters.dateTo) return null;
    return (visit: VisitLite) => {
      if (!visit.timeSlots[0]?.date) return false;
      const visitStartTime = format(
        new Date(
          `${format(visit.timeSlots[0].date, 'yyyy-MM-dd')}T${visit.timeSlots[0].startTime}`,
        ),
        appConfig.dateTimeFormat,
      );

      return (
        (!filters.dateFrom || visitStartTime >= filters.dateFrom) &&
        (!filters.dateTo || visitStartTime <= filters.dateTo)
      );
    };
  }, [filters.dateFrom, filters.dateTo]);

  const filteredVisits = useMemo(() => {
    const activeFilters = [
      filterByServiceType,
      filterByPatientName,
      filterByService,
      filterBySpecialist,
      filterByDateRange,
    ].filter(Boolean) as ((visit: VisitLite) => boolean)[];

    console.log('active filters: ', activeFilters);

    if (activeFilters.length === 0) return visits;

    return visits.filter((visit) => activeFilters.every((filterFn) => filterFn(visit)));
  }, [
    visits,
    filterByServiceType,
    filterByPatientName,
    filterByService,
    filterBySpecialist,
    filterByDateRange,
  ]);

  return {
    visitsFilterConfig,
    filteredVisits,
  };
};
