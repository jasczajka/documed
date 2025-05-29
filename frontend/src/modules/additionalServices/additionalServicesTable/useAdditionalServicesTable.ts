import { useMemo } from 'react';
import { AdditionalServiceWithDetails, Service } from 'shared/api/generated/generated.schemas';
import { FilterConfig } from 'shared/components/TableFilters';
import { AdditionalServiceFilters } from './AdditionalServicesTable';

const generateAddionalServicesFilterConfig = (
  allAdditionalServices: Service[],
  isPatient: boolean,
  hasPatientId: boolean,
  hasDoctorId: boolean,
): FilterConfig[] => [
  ...(!isPatient && !hasPatientId
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
    options: allAdditionalServices.map((service) => ({
      value: service.name,
      label: service.name,
    })),
  },

  ...(hasDoctorId
    ? []
    : [
        {
          name: 'specialist',
          label: 'Specjalista',
          type: 'text',
        } as const,
      ]),

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

export const useAdditionalServicesTable = ({
  additionalServices,
  filters,
  allAdditionalServices,
  isPatient,
  patientId,
  doctorId,
}: {
  additionalServices: AdditionalServiceWithDetails[];
  filters: AdditionalServiceFilters;
  allAdditionalServices: Service[];
  isPatient: boolean;
  patientId?: number;
  doctorId?: number;
}) => {
  const filterByPatientName = useMemo(() => {
    if (!filters.patientName) {
      return null;
    }
    const searchTerm = filters.patientName.toLowerCase();
    return (additionalService: AdditionalServiceWithDetails) =>
      additionalService.patientFullName.toLowerCase().includes(searchTerm);
  }, [filters.patientName]);

  const filterByService = useMemo(() => {
    if (!filters.service) return null;
    const searchTerm = filters.service.toLowerCase();
    return (additionalService: AdditionalServiceWithDetails) =>
      additionalService.serviceName.toLowerCase().includes(searchTerm);
  }, [filters.service]);

  const filterBySpecialist = useMemo(() => {
    if (!filters.fulfiller) {
      return null;
    }
    const searchTerm = filters.fulfiller.toLowerCase();
    return (additionalService: AdditionalServiceWithDetails) => {
      return additionalService.fulfillerFullName.toLowerCase().includes(searchTerm);
    };
  }, [filters.fulfiller]);

  const filterByDateRange = useMemo(() => {
    if (!filters.dateFrom && !filters.dateTo) {
      return null;
    }

    return (additionalService: AdditionalServiceWithDetails) => {
      if (!additionalService.date) {
        return false;
      }

      const additionalServiceDate = new Date(additionalService.date);

      const fromDate = filters.dateFrom ? new Date(filters.dateFrom) : null;
      const toDate = filters.dateTo ? new Date(filters.dateTo) : null;

      return (
        (!fromDate || additionalServiceDate >= fromDate) &&
        (!toDate || additionalServiceDate <= toDate)
      );
    };
  }, [filters.dateFrom, filters.dateTo]);

  const filteredAdditionalServices = useMemo(() => {
    const activeFilters = [
      filterByPatientName,
      filterByService,
      filterBySpecialist,
      filterByDateRange,
    ].filter(Boolean) as ((additionalService: AdditionalServiceWithDetails) => boolean)[];

    if (activeFilters.length === 0) return additionalServices;

    return additionalServices.filter((additionalService) =>
      activeFilters.every((filterFn) => filterFn(additionalService)),
    );
  }, [
    additionalServices,
    filterByPatientName,
    filterByService,
    filterBySpecialist,
    filterByDateRange,
  ]);

  const additionalServicesFilterConfig = useMemo(
    () =>
      generateAddionalServicesFilterConfig(
        allAdditionalServices,
        isPatient,
        !!patientId,
        !!doctorId,
      ),
    [allAdditionalServices, isPatient, patientId, doctorId],
  );

  return {
    additionalServicesFilterConfig,
    filteredAdditionalServices,
  };
};
