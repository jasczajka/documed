import { Service } from 'shared/api/generated/generated.schemas';

export const calculateNeededTimeSlots = (
  selectedServiceId: number | undefined,
  allServices: Service[],
  slotDurationMinutes: number,
): number => {
  if (!selectedServiceId) {
    return 1;
  }

  const service = allServices.find((s) => s.id === selectedServiceId);
  if (!service?.estimatedTime) {
    throw new Error(`Service with ID ${selectedServiceId} not found or missing estimated time`);
  }

  if (slotDurationMinutes <= 0) {
    throw new Error('Slot duration must be positive');
  }

  return Math.max(1, Math.ceil(service.estimatedTime / slotDurationMinutes));
};
