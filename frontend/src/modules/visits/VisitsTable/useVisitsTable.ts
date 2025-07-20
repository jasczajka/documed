import { useMemo } from 'react';
import {
  FacilityInfoReturnDTO,
  Service,
  VisitWithDetails,
  VisitWithDetailsStatus,
} from 'shared/api/generated/generated.schemas';
import { FilterConfig } from 'shared/components/TableFilters';
import { useFacilityStore } from 'shared/hooks/stores/useFacilityStore';
import { VisitsFilters } from './VisitsTable';

const generateVisitsFilterConfig = (
  services: Service[],
  allFacilities: FacilityInfoReturnDTO[],
  displayPatientColumn: boolean,
  displayDoctorColumn: boolean,
  disableFacilityFilter: boolean,
): FilterConfig[] => [
  {
    name: 'status',
    label: 'Status wizyty',
    type: 'select',
    options: [
      { value: VisitWithDetailsStatus.CANCELLED.toString(), label: 'Anulowana' },
      { value: VisitWithDetailsStatus.CLOSED.toString(), label: 'Zakończona' },
      { value: VisitWithDetailsStatus.IN_PROGRESS.toString(), label: 'W trakcie' },
      { value: VisitWithDetailsStatus.PLANNED.toString(), label: 'Zaplanowana' },
    ],
  },
  ...(displayPatientColumn
    ? [
        {
          name: 'patientName',
          label: 'Pacjent',
          type: 'text',
        } as const,
      ]
    : []),

  {
    name: 'service',
    label: 'Usługa',
    type: 'select',
    options: services.map((service) => ({
      value: service.name,
      label: service.name,
    })),
  },
  {
    name: 'facilityId',
    label: 'Placówka',
    options: allFacilities.map((facility) => ({
      value: facility.id.toString(),
      label: `${facility.city} ${facility.address}`,
    })),
    type: 'select',
    width: '350px',
    disabled: disableFacilityFilter,
  },
  ...(displayDoctorColumn
    ? [
        {
          name: 'specialist',
          label: 'Specjalista',
          type: 'text',
        } as const,
      ]
    : []),

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
  services,
  displayPatientColumn,
  displayDoctorColumn,
  disableFacilityFilter,
}: {
  visits: VisitWithDetails[];
  filters: VisitsFilters;
  services: Service[];
  displayPatientColumn: boolean;
  displayDoctorColumn: boolean;
  disableFacilityFilter: boolean;
}) => {
  const allFacilities = useFacilityStore((state) => state.facilities);
  const filterByStatus = useMemo(() => {
    if (!filters.status) {
      return null;
    }
    return (visit: VisitWithDetails) => visit.status.toString() === filters.status;
  }, [filters.status]);

  const filterByPatientName = useMemo(() => {
    if (!filters.patientName) {
      return null;
    }
    const searchTerm = filters.patientName.toLowerCase();
    return (visit: VisitWithDetails) => visit.patientFullName.toLowerCase().includes(searchTerm);
  }, [filters.patientName]);

  const filterByService = useMemo(() => {
    if (!filters.service) return null;
    const searchTerm = filters.service.toLowerCase();
    return (visit: VisitWithDetails) => visit.serviceName.toLowerCase().includes(searchTerm);
  }, [filters.service]);

  const filterBySpecialist = useMemo(() => {
    if (!filters.specialist) {
      return null;
    }
    const searchTerm = filters.specialist.toLowerCase();
    return (visit: VisitWithDetails) => {
      return visit.doctorFullName.toLowerCase().includes(searchTerm);
    };
  }, [filters.specialist]);

  const filterByFacility = useMemo(() => {
    if (!filters.facilityId) return null;
    return (visit: VisitWithDetails) => visit.facilityId?.toString() === filters.facilityId;
  }, [filters.facilityId]);

  const filterByDateRange = useMemo(() => {
    if (!filters.dateFrom && !filters.dateTo) {
      return null;
    }

    return (visit: VisitWithDetails) => {
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
      filterByFacility,
    ].filter(Boolean) as ((visit: VisitWithDetails) => boolean)[];

    if (activeFilters.length === 0) return visits;

    return visits.filter((visit) => activeFilters.every((filterFn) => filterFn(visit)));
  }, [
    visits,
    filterByStatus,
    filterByPatientName,
    filterByService,
    filterBySpecialist,
    filterByDateRange,
    filterByFacility,
  ]);

  const visitsFilterConfig = useMemo(
    () =>
      generateVisitsFilterConfig(
        services,
        allFacilities,
        displayPatientColumn,
        displayDoctorColumn,
        disableFacilityFilter,
      ),
    [services, displayPatientColumn, displayDoctorColumn],
  );

  return {
    visitsFilterConfig,
    filteredVisits,
  };
};
